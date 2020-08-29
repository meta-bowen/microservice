package com.wanbowen.mall.utils;

import com.aliyun.oss.OSSClient;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxDomOutputParser;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MxImageExport {

    private OSSClient ossClient;

    public MxImageExport(OSSClient ossClient) {
        this.ossClient = ossClient;
    }

    public String exportImage(String graphInfo, int weight, int height) throws Exception {
        String xml = graphInfo;
        int w = weight;
        int h = height;

        long t0 = System.currentTimeMillis();
        BufferedImage image = mxUtils.createBufferedImage(w, h, Color.WHITE);

        // Creates handle and configures anti-aliasing
        Graphics2D g2 = image.createGraphics();
        mxUtils.setAntiAlias(g2, true, true);

        // Parses request into graphics canvas
        mxGraphicsCanvas2D gc2 = new mxGraphicsCanvas2D(g2);
        parseXmlSax(xml, gc2);

        File file = new File("./exp-mxgraph/src/main/resources/static/imgs/imageexport.png");

        ImageIO.write(image, "png", file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "image/png", IOUtils.toByteArray(input));
        String imgUrl = post2OSS(multipartFile);
        return imgUrl;
    }

    public String post2OSS(MultipartFile file) throws IOException {
        // todo 将生成的png文件存入OSS服务器， 并删除对应的文件， 并获取存储的url
        if (!file.isEmpty()) {
            if (file.getContentType().contains("image")) {
                String temp = "images/upload/";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                temp=temp.concat(sdf.format(new Date()))+"/";
                // 获取图片的文件名
                String fileName1 = file.getOriginalFilename();
                // 获取图片的扩展名
                String extensionName = fileName1.substring(fileName1.indexOf("."));
                // 新的图片文件名 = 获取时间戳+"."图片扩展名
                String newFileName = String.valueOf(System.currentTimeMillis())  + extensionName;
                String pathAndFileName = temp+newFileName;

                String url = "ossClient.uploadByFile(pathAndFileName, file.getInputStream());";
                return url;
            }
        }
        return "";
    }

    /**
     * Creates and returns the image for the given request.
     *
     * @param xml
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    protected void parseXmlDom(String xml, mxICanvas2D canvas) {
        new mxDomOutputParser(canvas).read(mxXmlUtils.parseXml(xml)
                .getDocumentElement().getFirstChild());
    }

    /**
     * Creates and returns the image for the given request.
     *
     * @param xml
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    protected void parseXmlSax(String xml, mxICanvas2D canvas)
            throws SAXException, ParserConfigurationException, IOException {
        // Creates SAX handler for drawing to graphics handle
        mxSaxOutputHandler handler = new mxSaxOutputHandler(canvas);

        // Creates SAX parser for handler
        XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                .getXMLReader();
        reader.setContentHandler(handler);

        // Renders XML data into image
        reader.parse(new InputSource(new StringReader(xml)));
    }

    /**
     * The main method of the template test suite.
     *
     * @param args The array of runtime arguments.
     */
    /*public static void main(String[] args) throws Exception {
        MxImageExport export = new MxImageExport();
        System.out.println(MxImageExport.class.getResource(
                ""));
        export.exportImage(mxUtils.readFile(MxImageExport.class.getResource(
                "imageoutput.xml").getPath()), 551, 621);
    }*/

}

