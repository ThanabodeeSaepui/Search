import os, re, sys
import xml.etree.ElementTree as ET
import hashlib
import xml.dom.minidom
from datetime import datetime

def search(rootpath, name):
    result = ""
    for file in os.listdir(rootpath):
        d = os.path.abspath(os.path.join(rootpath, file))
        print("Process in " + d)
        if os.path.isdir(d):
            if re.search(name, os.path.basename(d)):
                result = d
                return result
            else:
                result = search(d, name)
                if result != "":
                    break
    return result


def pathtoxml(path):
    xmlstr = ""
    if os.path.isdir(path):
        xmlstr += f'<folder name="{os.path.basename(path)}">'
        for file in os.listdir(path):
            d = os.path.abspath(os.path.join(path, file))
            if os.path.isdir(d):
                xmlstr += pathtoxml(d)
            elif os.path.isfile(d):
                md5_hash = hashlib.md5()
                a_file = open(d, "rb")
                content = a_file.read()
                md5_hash.update(content)
                size = os.path.getsize(d)
                date = os.path.getmtime(d)
                date = datetime.fromtimestamp(date).strftime('%d/%m/%Y %H:%M:%S')
                xmlstr += f'<file md5="{md5_hash.hexdigest()}" size="{size}" date="{date}">{os.path.basename(d)}</file>'
        xmlstr += "</folder>"
    return xmlstr


def savexml(xmlstr):
    with open("save.xml", "wb") as f:
        f.write(xmlstr)


def searchXML(regex, xml):
    found = []
    if type(xml) != ET.Element:
        root = xml.getroot()
    else:
        root = xml
    for child in root:
        if child.tag == "folder":
            if re.match(regex, child.attrib["name"]):
                found.append(root.attrib["name"] + "/" + child.attrib["name"])
            child_found = searchXML(regex, child)
            for f in child_found:
                found.append(root.attrib["name"] + "/" + f)
        if child.tag == "file":
            if re.match(regex, child.text):
                found.append(root.attrib["name"] + "/" + child.text)
    return found


if __name__ == "__main__":
    if len(sys.argv) == 4:
        file_search = search(str(sys.argv[1]), str(sys.argv[2]))
        regex = sys.argv[3]
        print("*** " * 3 + "END OF PROCESS " + "*** " * 3)
        if file_search != "":
            print(f"Found {file_search}.\n")
            XML = pathtoxml(file_search)
            XML = xml.dom.minidom.parseString(XML)
            XML = XML.toprettyxml(encoding='utf-8')
            savexml(XML)
            saveXML = ET.parse("./save.xml")
            found = searchXML(regex, saveXML)
            if len(found) > 0:
                for i, f in enumerate(found):
                    print(f"Found {i+1}: {f}")
            else:
                print("Not found in saveXML")
        else:
            print("Not Found.")
    elif len(sys.argv) < 4:
        print("Error : Missing Search Argument.")
        print('''Example : python3 Search.py /home "Search Test" "hello.{0,}"''')
    else:
        print("Error : Must not have more than 3 Argument.")
