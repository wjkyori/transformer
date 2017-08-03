package org.transformer.support.dao.jpa.repository.callback;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.transformer.support.dao.jpa.search.SearchOperator;
import org.transformer.support.dao.jpa.search.Searchable;
import org.transformer.support.dao.jpa.search.filter.AndCondition;
import org.transformer.support.dao.jpa.search.filter.Condition;
import org.transformer.support.dao.jpa.search.filter.OrCondition;
import org.transformer.support.dao.jpa.search.filter.SearchFilter;

import javax.persistence.Query;

public class DefaultSearchCallback implements SearchCallback {

  /** The Constant paramPrefix. */
  private static final String paramPrefix = "param_";

  /** The alias. */
  private String alias;

  /** The alias with dot. */
  private String aliasWithDot;

  /**
   * Instantiates a new default search callback.
   */
  public DefaultSearchCallback() {
    this("");
  }

  /**
   * Instantiates a new default search callback.
   * 
   * @param alias
   *            the alias
   */
  public DefaultSearchCallback(String alias) {
    this.alias = alias;
    if (!StringUtils.isEmpty(alias)) {
      this.aliasWithDot = alias + ".";
    } else {
      this.aliasWithDot = "";
    }
  }

  /**
   * Gets the alias.
   * 
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Gets the alias with dot.
   * 
   * @return the alias with dot
   */
  public String getAliasWithDot() {
    return aliasWithDot;
  }

  @Override
  public void prepareQl(StringBuilder ql, Searchable search) {
    if (!search.hasSearchFilter()) {
      return;
    }

    int paramIndex = 1;
    for (SearchFilter searchFilter : search.getSearchFilters()) {

      if (searchFilter instanceof Condition) {
        Condition condition = (Condition) searchFilter;
        if (condition.getOperator() == SearchOperator.custom) {
          continue;
        }
      }

      ql.append(" and ");

      paramIndex = genCondition(ql, paramIndex, searchFilter);

    }
  }

  /**
   * Gen condition.
   * 
   * @param ql
   *            the ql
   * @param paramIndex
   *            the param index
   * @param searchFilter
   *            the search filter
   * @return the int
   */
  private int genCondition(StringBuilder ql, int index, SearchFilter searchFilter) {
    int paramIndex = index;
    boolean needAppendBracket = searchFilter instanceof OrCondition
        || searchFilter instanceof AndCondition;

    if (needAppendBracket) {
      ql.append("(");
    }

    if (searchFilter instanceof Condition) {
      Condition condition = (Condition) searchFilter;
      // 自定义条件
      String entityProperty = condition.getEntityProperty();
      String operatorStr = condition.getOperatorStr();
      // 实体名称
      ql.append(getAliasWithDot());
      ql.append(entityProperty);
      // 操作符
      ql.append(" ");
      //如果是自定义ql语句，值不为空时放入参数值
      if (condition.getOperator() == SearchOperator.customQl) {
        if (null != condition.getValue()) {
          if (!condition.isUnaryFilter()) {
            ql.append(" :");
            ql.append(paramPrefix);
            ql.append(paramIndex++);
            return paramIndex;
          }
        }
      } else {
        // 1、如果是自定义查询符号，则使用SearchPropertyMappings中定义的默认的操作符
        ql.append(operatorStr);

        if (!condition.isUnaryFilter()) {
          ql.append(" :");
          ql.append(paramPrefix);
          ql.append(paramIndex++);
          return paramIndex;
        }

      }
    } else if (searchFilter instanceof OrCondition) {
      boolean isFirst = true;
      for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
        if (!isFirst) {
          ql.append(" or ");
        }
        paramIndex = genCondition(ql, paramIndex, orSearchFilter);
        isFirst = false;
      }
    } else if (searchFilter instanceof AndCondition) {
      boolean isFirst = true;
      for (SearchFilter andSearchFilter : ((AndCondition) searchFilter).getAndFilters()) {
        if (!isFirst) {
          ql.append(" and ");
        }
        paramIndex = genCondition(ql, paramIndex, andSearchFilter);
        isFirst = false;
      }
    }

    if (needAppendBracket) {
      ql.append(")");
    }
    return paramIndex;
  }

  @Override
  public void setValues(Query query, Searchable search) {

    int paramIndex = 1;

    for (SearchFilter searchFilter : search.getSearchFilters()) {
      paramIndex = setValues(query, searchFilter, paramIndex);
    }

  }

  /**
   * Sets the values.
   * 
   * @param query
   *            the query
   * @param searchFilter
   *            the search filter
   * @param paramIndex
   *            the param index
   * @return the int
   */
  private int setValues(Query query, SearchFilter searchFilter, int index) {
    int paramIndex = index;
    if (searchFilter instanceof Condition) {

      Condition condition = (Condition) searchFilter;
      if (condition.getOperator() == SearchOperator.custom) {
        return paramIndex;
      }
      if (condition.getOperator() == SearchOperator.customQl) {
        if (null != condition.getValue()) {
          query.setParameter(paramPrefix + paramIndex++,
              formtValue(condition, condition.getValue()));
          return paramIndex;
        } else {
          //自定义ql查询参数为空直接返回
          return paramIndex;
        }
      }
      if (condition.isUnaryFilter()) {
        return paramIndex;
      }
      query.setParameter(paramPrefix + paramIndex++, formtValue(condition, condition.getValue()));

    } else if (searchFilter instanceof OrCondition) {

      for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
        paramIndex = setValues(query, orSearchFilter, paramIndex);
      }

    } else if (searchFilter instanceof AndCondition) {
      for (SearchFilter andSearchFilter : ((AndCondition) searchFilter).getAndFilters()) {
        paramIndex = setValues(query, andSearchFilter, paramIndex);
      }
    }
    return paramIndex;
  }

  /**
   * Formt value.
   * 
   * @param condition
   *            the condition
   * @param value
   *            the value
   * @return the object
   */
  private static Object formtValue(Condition condition, Object value) {
    SearchOperator operator = condition.getOperator();
    if (operator == SearchOperator.like || operator == SearchOperator.notLike) {
      return "%" + value + "%";
    }
    if (operator == SearchOperator.prefixLike || operator == SearchOperator.prefixNotLike) {
      return value + "%";
    }

    if (operator == SearchOperator.suffixLike || operator == SearchOperator.suffixNotLike) {
      return "%" + value;
    }
    return value;
  }

  @Override
  public void setPageable(Query query, Searchable search) {
    if (search.hasPageable()) {
      Pageable pageable = search.getPage();
      query.setFirstResult(pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
    }
  }

  @Override
  public void prepareOrder(StringBuilder ql, Searchable search) {
    if (search.hashSort()) {
      ql.append(" order by ");
      for (Sort.Order order : search.getSort()) {
        ql.append(String.format("%s%s %s, ", getAliasWithDot(), order.getProperty(),
            order.getDirection().name().toLowerCase()));
      }

      ql.delete(ql.length() - 2, ql.length());
    }
  }

}
