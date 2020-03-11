import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.sql.SQLException;


public class xmlParser {
    //DOM
    public static void writeXML(String[] arr_col_names,String[][] fullDataFromSet,String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;
            File file = new File(filePath);
            if (file.length() == 0) {
                document = factory.newDocumentBuilder().newDocument();
                Element root = document.createElement("root");
                document.appendChild(root);
            } else {
                document = builder.parse(filePath);
            }
            document.getDocumentElement().normalize();

            fillTable(document, arr_col_names, fullDataFromSet);
            document.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (SAXParseException exception) {
            System.out.println("ERRROR");
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    private static void fillTable(Document doc, String[] arr_col_names, String[][] fullDataFromSet) throws SQLException {
        NodeList table = doc.getElementsByTagName("table");
        Element lang = null;
        int i = table.getLength();
        lang = (Element) table.item(i - 1);

        Element tr_head = doc.createElement("tr");
        for(int it = 0;it < arr_col_names.length;it++){
            Element th = doc.createElement("th");
            th.appendChild(doc.createTextNode(arr_col_names[it]));
            tr_head.appendChild(th);
        }
        lang.appendChild(tr_head);
        //rows
        for (int it = 0;it <fullDataFromSet[0].length;it++){
            Element tr = doc.createElement("tr");
            //col
            for(int j = 0;j < fullDataFromSet.length; j++){
                Element th = doc.createElement("th");
                th.appendChild(doc.createTextNode(fullDataFromSet[j][it]));
                tr.appendChild(th);
            }
            lang.appendChild(tr);
        }
    }
}
