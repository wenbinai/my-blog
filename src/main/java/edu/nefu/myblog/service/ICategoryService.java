package edu.nefu.myblog.service;

import edu.nefu.myblog.pojo.Category;
import edu.nefu.myblog.response.ResponseResult;

public interface ICategoryService {

    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult listCategories(int page, int size);

    ResponseResult updateCategory(String categoryId, Category category);

    ResponseResult deleteCategory(String categoryId);
}
