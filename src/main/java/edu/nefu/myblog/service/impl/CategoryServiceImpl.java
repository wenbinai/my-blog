package edu.nefu.myblog.service.impl;

import edu.nefu.myblog.dao.CategoryDao;
import edu.nefu.myblog.pojo.Category;
import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.ICategoryService;
import edu.nefu.myblog.util.Constants;
import edu.nefu.myblog.util.SnowflakeIdWorker;
import edu.nefu.myblog.util.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private CategoryDao categoryDao;


    @Override
    public ResponseResult addCategory(Category category) {
        // 对上传的category信息进行检验
        // 分类名称
        if (TextUtil.isEmpty(category.getName())) {
            return ResponseResult.FAILED("分类名称不能为空");
        }
        // 分类描述
        if (TextUtil.isEmpty(category.getDescription())) {
            return ResponseResult.FAILED("分类描述不能为空");
        }
        // 分类字母表示
        if (TextUtil.isEmpty(category.getPinyin())) {
            return ResponseResult.FAILED("分类字母表示不能为空");
        }
        // 补充分类信息
        //补全数据
        category.setId(idWorker.nextId() + "");
        category.setStatus("1");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        category.setOrder(1);
        //保存数据
        categoryDao.save(category);
        //返回结果
        return ResponseResult.SUCCESS("添加分类成功");
    }

    @Override
    public ResponseResult getCategory(String categoryId) {
        Category categoryFromDB = categoryDao.findOneById(categoryId);
        if (categoryFromDB == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        ResponseResult responseResult = ResponseResult.SUCCESS("查找成功");
        responseResult.setData(categoryFromDB);
        return responseResult;
    }

    @Override
    public ResponseResult listCategories(int page, int size) {
        // 检查page,size参数
        if (page < Constants.Page.DEFAULT_PAGE)
            page = Constants.Page.DEFAULT_PAGE;
        if (size < Constants.Page.DEFAULT_SIZE)
            size = Constants.Page.DEFAULT_SIZE;

//        Sort sort = new Sort.Order(Sort.Direction.DESC);

//        Sort sort = new Sort(Sort.Direction.DESC, new String[]{"createTime", "order"});
        ArrayList<String> strings = new ArrayList<>();
        strings.add("createTime");
        strings.add("order");
        Sort sort = Sort.by(Sort.Direction.DESC, "order", "createTime");

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Category> all = categoryDao.findAll(pageable);

        ResponseResult responseResult = ResponseResult.SUCCESS("获取分类列表成功.");
        responseResult.setData(all);
        //返回结果
        return responseResult;

    }

    @Override
    public ResponseResult updateCategory(String categoryId, Category category) {
        Category categoryFromDB = categoryDao.findOneById(categoryId);
        if (categoryFromDB == null) {
            return ResponseResult.FAILED("分类不存在");
        }
        if (!TextUtil.isEmpty(category.getName())) {
            categoryFromDB.setName(category.getName());
        }
        if (!TextUtil.isEmpty(category.getPinyin())) {
            categoryFromDB.setPinyin(category.getPinyin());
        }
        if (!TextUtil.isEmpty(category.getDescription())) {
            categoryFromDB.setDescription(category.getDescription());
        }
        categoryFromDB.setUpdateTime(new Date());
        categoryDao.save(categoryFromDB);
        ResponseResult responseResult = ResponseResult.SUCCESS("修改成功");

        return responseResult;
    }

    /**
     * 删除分类
     * 并不是真的删除, 只是改变状态
     *
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = categoryDao.deleteByUpdateState(categoryId);
        if (result < 0) {
            return ResponseResult.FAILED("删除失败");
        }
        return ResponseResult.SUCCESS("删除成功");
    }


}
