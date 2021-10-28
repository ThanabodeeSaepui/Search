import os, re, sys
import xml.etree.ElementTree as ET


def search(rootpath, name):
    result = list()
    for file in os.listdir(rootpath):
        d = os.path.abspath(os.path.join(rootpath, file))
        print("Process in " + d)
        if re.search(name, os.path.basename(d)):
            result.append(d)
        if os.path.isdir(d):
            result.extend(search(d, name))
    return result


def pathtoxml(path):
    xmlstr = str()
    if os.path.isdir(path):
        xmlstr += f'<folder name="{os.path.basename(path)}">\n\t'
        for file in os.listdir(path):
            d = os.path.abspath(os.path.join(path, file))
            if os.path.isdir(d):
                xmlstr += pathtoxml(d)
            elif os.path.isfile(d):
                xmlstr += f"\t<file>{os.path.basename(d)}</file>\n"
        xmlstr += "</folder>\n"
    return xmlstr


def savexml(xmlstr):
    with open("save.xml", "w", encoding="utf-8") as f:
        f.write('<?xml version="1.0" encoding="UTF-8"?>\n' + xmlstr)


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
    if len(sys.argv) == 3:
        file_search = search(str(sys.argv[1]), str(sys.argv[2]))
        print("*** " * 3 + "END OF PROCESS " + "*** " * 3)
        if len(file_search) > 0:
            print(f"Found {len(file_search)}.\n")
            xml = str()
            for i in file_search:
                # print(i)
                xml += pathtoxml(i)
            savexml(xml)
            saveXML = ET.parse("./save.xml")
            found = searchXML(r"^hello.{0,}", saveXML)
            if len(found) > 0:
                for i, f in enumerate(found):
                    print(f"Found {i+1}: {f}")
            else:
                print("Not found in saveXML")
        else:
            print("Not Found.")
    elif len(sys.argv) < 3:
        print("Error : Missing Search Argument.")
    else:
        print("Error : Must not have more than 2 Argument.")
