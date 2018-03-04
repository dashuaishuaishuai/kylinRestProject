package com.kylin.rest;

import org.apache.commons.cli.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 华安  mashuai_bj@si-tech.com.cn
 * @Title:
 * @Date: Create in 16:01 2018/2/28
 * @Description:
 */
public class KylinRest {

    public static void main(String[] args) throws ParseException {
        // Create a Parser
        Options options = new Options();
        //短选项，长选项，选项后是否有参数，描述
        Option option = new Option("l", "url", true, "请求的url");
        option.setRequired(false);//必须设置
        options.addOption(option);

        option = new Option("m", "methodType", true,
                "请求的方法类型(get or post)");
        option.setRequired(false);
        options.addOption(option);


        option = new Option("f", "file", true,
                "文件的路径名称");
        option.setRequired(false);
        options.addOption(option);


        option = new Option("h", "help", false, "display help text");
        options.addOption(option);

        //CommandLineParser parser = new PosixParser();//Posix风格
        CommandLineParser parser = new GnuParser();//gun风格
        CommandLine commandLine = parser.parse(options, args);

        //判断
        if (commandLine.hasOption('h')) {
            //格式化输出
            new HelpFormatter().printHelp("flume-ng agent", options, true);
            return;
        }
        if (!commandLine.hasOption('f')) {
            //获取参数
            new HelpFormatter().printHelp("flume-ng agent", options, true);
            return;

        }
        if (!commandLine.hasOption('m')) {
            //获取参数
            new HelpFormatter().printHelp("flume-ng agent", options, true);
            return;
        }
        if (!commandLine.hasOption('l')) {
            //获取参数
            new HelpFormatter().printHelp("flume-ng agent", options, true);
            return;
        }

        String file = commandLine.getOptionValue('f');

        String methodType = commandLine.getOptionValue('m');

        String  url = commandLine.getOptionValue('l');

        String requestBody = getNeedData(file);
        try {
            String result = call(url,methodType,requestBody);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String call(final String url, final String methodType,final String requestBody) throws IOException {
        String result = null;
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(methodType);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            String username = "ADMIN";
            String password = "KYLIN";
            String input = username + ":" + password;
            String encoding = new sun.misc.BASE64Encoder().encode(input.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            conn.connect();

//            String obj = "{\"startTime\":\"0\",\"endTime\":\"1472832000000\",\"buildType\":\"BUILD\"}";
//            String obj = "{\"startTime\":\"1472832000000\",\"endTime\":\"1473177600000\",\"buildType\":\"BUILD\"}";
//            String obj = "{\"startTime\":\"1472832000000\",\"endTime\":\"1473177600000\",\"buildType\":\"BUILD\"}";
//            String obj = "{\"startTime\":\"1473177600000\",\"endTime\":\"1473465600000\",\"buildType\":\"REFRESH\"}";
//            String obj = "{\"startTime\":\"1472832000000\",\"endTime\":\"1473177600000\",\"buildType\":\"MERGE\"}";
            conn.setRequestProperty("Content-Length", requestBody.toString().getBytes().length + "");  //设置文件请求的长度

            OutputStream out = conn.getOutputStream();
            out.write(requestBody.toString().getBytes());
            out.flush();
            out.close();

//            conn.setRequestProperty("startTime", "0");
//            conn.setRequestProperty("endTime", "1472832000000");
//            conn.setRequestProperty("buildType", "BUILD");

            if (200 != conn.getResponseCode()) {
                isr = new InputStreamReader(conn.getErrorStream());
                br = new BufferedReader(isr);
                String line = null;
                StringBuilder errorMsg = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    errorMsg.append(line).append("\\n");
                }
                return errorMsg.toString();
            }
            byte[] temp = new byte[conn.getInputStream().available()];
            if (conn.getInputStream().read(temp) != -1) {
                result = new String(temp);
            }
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return result;
    }

    public static String getNeedData(String filePathAndName){
        StringBuffer sbf = new StringBuffer();
        File file =new File(filePathAndName);
        BufferedReader reader = null;
        try {
            // 一次读一个字符
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                sbf.append(tempString);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbf.toString();
    }
} 