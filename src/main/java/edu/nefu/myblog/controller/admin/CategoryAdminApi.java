package edu.nefu.myblog.controller.admin;

import edu.nefu.myblog.pojo.Category;
import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {
    @Autowired
    private ICategoryService categoryService;

    @PreAuthorize("@permission.adminPermission()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取全部分类
     * 分页: 页码 每页大小
     *
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult listCategory(@PathVariable("page") int page,
                                       @PathVariable("size") int size) {

        return categoryService.listCategories(page, size);

    }


    /**
     * 修改分类
     * 修改内容: 拼音, 名称, 描述, 排序, 更新时间
     *
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.adminPermission()")
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId,
                                         @RequestBody Category category) {
        return categoryService.updateCategory(categoryId, category);
    }

    @PreAuthorize("@permission.adminPermission()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }
}
