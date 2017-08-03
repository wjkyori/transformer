package org.transformer.support.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.support.entity.AbstractEntity;
import org.transformer.support.service.BaseService;

/**
 * 分页基础数据控制器.
 */
public abstract class DataTableController<M extends AbstractEntity<Long>, S extends BaseService<M>>
    extends BaseController<M, S> {

  /**
   * 根据页面条件（包括分页信息）查询符合条件的记录数.
   * @param searchable 查询条件对象
   * @param pageIndex  页索引
   * @param pageSize  页面大小
   * @param sortType  排序类型 asc 或 desc
   * @param sortName  排序字段 
   * @return {String} json字符串
   */
  @RequestMapping("/dataTable")
  @ResponseBody
  public String dataTable(Searchable searchable, int pageIndex, int pageSize, String sortType,
      String sortName) {
    Sort sort = new Sort("asc".equals(sortType) ? Direction.ASC : Direction.DESC, sortName);
    Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
    searchable.setPage(pageable);
    Page<M> page = this.getPageData(searchable);
    //构造返回对象
    return GridJson
        .addParam(pageIndex, page.getTotalPages(), page.getTotalElements(), page.getContent())
        .toJson();
  }

  /**
   * 获取分页数据.
   * @param searchable 查询条件
   * @return 分页数据
   */
  public abstract Page<M> getPageData(Searchable searchable);
}
