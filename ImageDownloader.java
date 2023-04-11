/**
 * This is a program for downloading pictures in batches,
 * it only supports http and https protocols,
 * I wish you a happy use!
 * @author StriveMoring
 * GitHub:https://github.com/StriveMoring/ImageDownloader
 */
package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageDownloader {
    public static void main(String[] args) throws Exception {
        String targetUrl = "https://example.com"; // 目标网站的url
        String fileExt = "png"; // 指定的文件扩展名
        String saveUrlListPath = "F:/example/list.txt"; // 保存url列表的文件路径
        String saveDirPath = "F:/Test"; // 图片文件保存目录
        int minWidth = 60; // 最小宽度
        int maxWidth = 600; // 最大宽度

        // 1. 访问目标页面
        HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // 2. 读取页面内容
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        // 3. 提取所有符合条件的图片url
        List<String> imgUrlList = new ArrayList<>();
        String regex = "(https?://\\S+\\." + fileExt + ")";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content.toString());
        while (matcher.find()) {
            String imgUrl = matcher.group(1);
            imgUrlList.add(imgUrl);
        }

        // 4. 保存url列表到文件
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveUrlListPath))) {
            for (String imgUrl : imgUrlList) {
                writer.println(imgUrl);
            }
        }

        // 5. 下载所有符合条件的图片
        for (String imgUrl : imgUrlList) {
            HttpURLConnection imgConn = (HttpURLConnection) new URL(imgUrl).openConnection();
            imgConn.setRequestMethod("GET");
            imgConn.connect();
            try (InputStream in = imgConn.getInputStream()) {
                BufferedImage image = ImageIO.read(in);
                if (image.getWidth() >= minWidth && image.getWidth() <= maxWidth) {
                    File saveFile = new File(saveDirPath, imgUrl.substring(imgUrl.lastIndexOf("/") + 1));
                    ImageIO.write(image, fileExt, saveFile);
                }
            }
        }
    }
}
