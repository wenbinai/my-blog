package edu.nefu.myblog.service.impl;

import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.ImageService;
import edu.nefu.myblog.util.Constants;
import edu.nefu.myblog.util.SnowflakeIdWorker;
import edu.nefu.myblog.util.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Value("${my.blog.image.save-path}")
    private String imagePath;
    @Value("${my.blog.image.max-size}")
    private long maxSize;

    @Autowired
    private SnowflakeIdWorker idWorker;

    /**
     * 上传路径: 可以配置, 再配置文件中配置
     * 上传内容: 命名: 可以用ID, 同时每天用一个文件夹保存
     * 限制上传文件大小: 通过配置文件配置
     * 保存记录到数据库中
     * ID: /存储路径/url/
     *
     * @param file
     * @return
     */
    @Override
    public ResponseResult upload(MultipartFile file) {
        log.info("imagePath-->" + imagePath);
//        1. 判断文件是否存在;
        if (file == null) {
            return ResponseResult.FAILED("图片不可以为空");
        }
//        2. 判断文件类型(png, jpg, gif);
        String contentType = file.getContentType();
        if (TextUtil.isEmpty(contentType)) {
            return ResponseResult.FAILED("图片格式错误");
        }

        String type = null;

        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename-->" + originalFilename);
        if (Constants.ImageType.TYPE_PNG_WITH_PREFIX.equals(contentType) ||
                originalFilename.endsWith(Constants.ImageType.TYPE_JPG)) {
            type = Constants.ImageType.TYPE_PNG;
        } else if (Constants.ImageType.TYPE_GIF_WITH_PREFIX.equals(contentType) ||
                originalFilename.endsWith(Constants.ImageType.TYPE_PNG)) {
            type = Constants.ImageType.TYPE_GIF;
        } else if (Constants.ImageType.TYPE_JPG_WITH_PREFIX.equals(contentType) ||
                originalFilename.endsWith(Constants.ImageType.TYPE_GIF)) {
            type = Constants.ImageType.TYPE_JPG;
        } else {
            return ResponseResult.FAILED("不支持此图片类型");
        }

//        3. 获取文件相关属性(文件类型, 文件名称);


//        限制文件大小
        long size = file.getSize();
        log.info("maxSize==> " + maxSize + " size ==>" + size);
        if (size > maxSize) {
            return ResponseResult.FAILED("图片大小过大");
        }
//        4. 自定义规则重新命名;
        long currentTimeMillis = System.currentTimeMillis();
        String currnetDay = simpleDateFormat.format(currentTimeMillis);
        log.info("current day ==> " + currnetDay);
        String dayPath = imagePath + File.separator + currnetDay;
        File dayPathFile = new File(dayPath);
        // 判断日期文件是否存在
        if (!dayPathFile.exists()) {
            dayPathFile.mkdirs();
        }
        String id = String.valueOf(idWorker.nextId());
        log.info("id ==>" + id);
        String targetPath = dayPath +
                File.separator + type + File.separator + id +
                "." + type;

        File targetFile = new File(targetPath);
        // 判断类型文件是否存在
        if (!targetFile.getParentFile().exists()) {
            targetFile.mkdirs();
        }
        log.info("targetFile ==> " + targetFile);

//        5. 保存文件;
        try {
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            file.transferTo(targetFile);
            //        6. 记录文件;

//        7. 返回结果;
            // 第一个是访问路径
            // 第二个是名称
            Map<String, String> result = new HashMap<>();
            String resultPath = currentTimeMillis + "_" + id + "." + type;
            result.put("path", resultPath);
            result.put("name", originalFilename);
            log.info("resultPath ==>" + resultPath);
            log.info("originalFilename==>" + originalFilename);
            ResponseResult responseResult = ResponseResult.SUCCESS("文件上传成功");
            responseResult.setData(result);
            log.info("文件上传成功并结束");
            return responseResult;
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.FAILED("图片上传失败, 请稍后重新上传");
        }

    }

    @Override
    public void viewImage(HttpServletResponse response, String imageId) {
        // 日期
        String[] paths = imageId.split("_");
        String dayValue = paths[0];
        String format = simpleDateFormat.format(Long.parseLong(dayValue));
        String name = paths[1];

        // 类型
        String type = name.substring(name.length() - 3);
        String targetPath = imagePath + File.separator
                + format + File.separator + type +
                File.separator + name;

        log.info("get image path -->" + targetPath);
        File file = new File(targetPath);
        OutputStream writer = null;
        FileInputStream fos = null;

        try {
            response.setContentType("image/" + type);
            writer = response.getOutputStream();
            // 读取
            fos = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = fos.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
