package org.transformer.support.controller;

import org.transformer.util.JsonMapper;

import java.io.Serializable;

public class GridJson implements Serializable {

  /**
   * 序列号.
   */
  private static final long serialVersionUID = 9139192104751877415L;

  private static JsonMapper jsonMapper = new JsonMapper();

  /** 当前页数.*/
  private int page;
  /** 总页数. */
  private int total;
  /** 记录数.*/
  private Long records;

  /** 需要转换的对象. */
  private Object rows;

  public Object getRows() {
    return rows;
  }

  public void setRows(Object rows) {
    this.rows = rows;
  }

  /**
   * 通过参数生成gridJosn对象.
   * @param page 页索引
   * @param total 总页数
   * @param records 总记录数
   * @param obj 需要展示的对象(是个对象数组)
   * @return 表格数据
   */
  public static GridJson addParam(int page, int total, Long records, Object obj) {
    return new GridJson(page, total, records, obj);
  }

  public String toJson() {
    return jsonMapper.toJson(this);
  }

  public GridJson() {
    super();
  }

  /**
   * 够着函数.
   * @param page 页索引
   * @param total 总页数
   * @param records 总记录数
   * @param rows 需要展示的对象(是个对象数组)
   */
  public GridJson(int page, int total, Long records, Object rows) {
    this.page = page;
    this.total = total;
    this.records = records;
    this.rows = rows;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Long getRecords() {
    return records;
  }

  public void setRecords(Long records) {
    this.records = records;
  }

}
