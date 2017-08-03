package org.transformer.support.dao.jpa.search;

import org.apache.commons.lang3.StringUtils;
import org.transformer.support.dao.jpa.search.exception.SearchException;

import java.util.Arrays;

/**
 * 查询操作符.
 *
 */
public enum SearchOperator {

  /** The eq. */
  eq("等于", "="),
  /** The ne. */
  ne("不等于", "!="),

  /** The gt. */
  gt("大于", ">"),
  /** The gte. */
  gte("大于等于", ">="),
  /** The lt. */
  lt("小于", "<"),
  /** The lte. */
  lte("小于等于", "<="),

  /** The prefix like. */
  prefixLike("前缀模糊匹配", "like"),
  /** The prefix not like. */
  prefixNotLike("前缀模糊不匹配", "not like"),

  /** The suffix like. */
  suffixLike("后缀模糊匹配", "like"),
  /** The suffix not like. */
  suffixNotLike("后缀模糊不匹配", "not like"),

  /** The like. */
  like("模糊匹配", "like"),
  /** The not like. */
  notLike("不匹配", "not like"),

  /** The is null. */
  isNull("空", "is null"),
  /** The is not null. */
  isNotNull("非空", "is not null"),

  /** The in. */
  in("包含", "in"),
  /** The not in. */
  notIn("不包含", "not in"),
  /** The custom. */
  custom("自定义默认的", null),
  /** The custom ql. */
  customQl("自定义的ql语句", null);

  /** The info. */
  private final String info;

  /** The symbol. */
  private final String symbol;

  /**
   * Instantiates a new search operator.
   * 
   * @param info
   *            the info
   * @param symbol
   *            the symbol
   */
  SearchOperator(final String info, String symbol) {
    this.info = info;
    this.symbol = symbol;
  }

  /**
   * Gets the info.
   * 
   * @return the info
   */
  public String getInfo() {
    return info;
  }

  /**
   * Gets the symbol.
   * 
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * To string all operator.
   * 
   * @return the string
   */
  public static String toStringAllOperator() {
    return Arrays.toString(SearchOperator.values());
  }

  /**
   * 操作符是否允许为空.
   * 
   * @param operator
   *            the operator
   * @return true, if is allow blank value
   */
  public static boolean isAllowBlankValue(final SearchOperator operator) {
    return operator == SearchOperator.isNotNull || operator == SearchOperator.isNull;
  }

  /**
   * Value by symbol.
   * 
   * @param symbol
   *            the symbol
   * @return the search operator
   * @throws SearchException
   *             the search exception
   */
  public static SearchOperator valueBySymbol(String symbol) throws SearchException {
    symbol = formatSymbol(symbol);
    for (SearchOperator operator : values()) {
      if (operator.getSymbol().equals(symbol)) {
        return operator;
      }
    }

    throw new SearchException("SearchOperator not method search operator symbol : " + symbol);
  }

  /**
   * Format symbol.
   * 
   * @param symbol
   *            the symbol
   * @return the string
   */
  private static String formatSymbol(String symbol) {
    if (StringUtils.isBlank(symbol)) {
      return symbol;
    }
    return symbol.trim().toLowerCase().replace("  ", " ");
  }
}
