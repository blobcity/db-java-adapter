/*
 * Copyright 2014, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.search;

import com.blobcity.db.exceptions.InternalAdapterException;
import static com.blobcity.db.search.ParamOperator.BETWEEN;
import static com.blobcity.db.search.ParamOperator.EQ;
import static com.blobcity.db.search.ParamOperator.GT;
import static com.blobcity.db.search.ParamOperator.GT_EQ;
import static com.blobcity.db.search.ParamOperator.IN;
import static com.blobcity.db.search.ParamOperator.LT;
import static com.blobcity.db.search.ParamOperator.LT_EQ;
import static com.blobcity.db.search.ParamOperator.NOT_EQ;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to create Search parameters to be used as the WHERE clause in a query which helps in filtering result sets.
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
public class SearchParam implements ArrayJsonable, Sqlable {

    private final String paramName;
    private ParamOperator condition;
    private JsonArray args;
    private final Map<String, Object> baseParamMap;
    private final List<SearchOperator> operators;
    private final List<SearchParam> conditions;

    /**
     * Private initializer for the class. Use {@link #create(java.lang.String)} for creating an object
     *
     * @param paramName name of the parameter which is being searched
     */
    private SearchParam(final String paramName) {
        this.paramName = paramName;
        this.operators = new ArrayList<SearchOperator>();
        this.conditions = new ArrayList<SearchParam>();
        this.baseParamMap = new HashMap<String, Object>();
        this.baseParamMap.put("c", paramName);
    }

    /**
     * Creates a new {@link SearchParam} for a parameter
     *
     * @param paramName name of the parameter which is being searched
     * @return an instance of {@link SearchParam}
     */
    public static SearchParam create(final String paramName) {
        return new SearchParam(paramName);
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#IN} along with the arguments for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#IN
     * @param args arguments for the IN condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam in(final Object... args) {
        this.condition = ParamOperator.IN;
        addArgs(args);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#EQ} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#EQ
     * @param arg argument for the EQ condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam eq(final Object arg) {
        this.condition = ParamOperator.EQ;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#NOT_EQ} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#NOT_EQ
     * @param arg argument for the NOT_EQ condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam noteq(final Object arg) {
        this.condition = ParamOperator.NOT_EQ;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#GT} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#GT
     * @param arg argument for the GT condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam gt(final Object arg) {
        this.condition = ParamOperator.GT;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#LT} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#LT
     * @param arg argument for the LT condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam lt(final Object arg) {
        this.condition = ParamOperator.LT;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#GT_EQ} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#GT_EQ
     * @param arg argument for the GT_EQ condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam gteq(final Object arg) {
        this.condition = ParamOperator.GT_EQ;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#LT_EQ} along with the argument for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#LT_EQ
     * @param arg argument for the LT_EQ condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam lteq(final Object arg) {
        this.condition = ParamOperator.LT_EQ;
        addArgs(arg);
        return updateBaseParams();
    }

    /**
     * Sets the condition for this search param as {@link ParamOperator#BETWEEN} along with the arguments for it. Any earlier conditions and arguments on this
     * {@link SearchParam} will be replaced.
     *
     * @see ParamOperator#BETWEEN
     * @param arg1 left hand bound argument for the BETWEEN condition
     * @param arg2 right hand bound argument for the BETWEEN condition
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam between(final Object arg1, final Object arg2) {
        this.condition = ParamOperator.BETWEEN;
        addArgs(arg1, arg2);
        return updateBaseParams();
    }

    /**
     * Allows other {@link SearchParam}s to be linked to the existing one using an {@link SearchOperator#AND} operator
     *
     * @see SearchOperator#AND
     * @param param another {@link  SearchParam} to be linked to the existing one
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam and(final SearchParam param) {
        operators.add(SearchOperator.AND);
        conditions.add(param);
        return this;
    }

    /**
     * Allows other {@link SearchParam}s to be linked to the existing one using an {@link SearchOperator#OR} operator
     *
     * @see SearchOperator#OR
     * @param param another {@link  SearchParam} to be linked to the existing one
     * @return updated current instance of {@link SearchParam}
     */
    public SearchParam or(final SearchParam param) {
        operators.add(SearchOperator.OR);
        conditions.add(param);
        return this;
    }

    @Override
    public JsonArray asJson() {
        final JsonArray jsonArray = new JsonArray();

        final JsonObject baseJson = new JsonObject();
        baseJson.addProperty("c", paramName);
        baseJson.addProperty("x", condition.toString());
        baseJson.add("v", getJsonPrimitive(padJsonArgs()));

        jsonArray.add(baseJson);

        if (!operators.isEmpty()) {
            final int operatorCount = operators.size();
            final int conditionCount = conditions.size();
            for (int i = 0; i < operatorCount && i < conditionCount; i++) {
                jsonArray.add(new JsonPrimitive(operators.get(i).toString()));
                jsonArray.add(conditions.get(i).asJson());
            }
        }
        return jsonArray;
    }

    @Override
    public String asSql() {
        final StringBuffer sb = new StringBuffer("`").append(paramName).append("` ").append(condition.asSql());

        switch (condition) {
            case EQ:
            case NOT_EQ:
            case LT:
            case LT_EQ:
            case GT:
            case GT_EQ:
                sb.append(" ").append(padSqlArg(args.get(0)));
                break;
            case BETWEEN:
                sb.append(" ").append(padSqlArg(args.get(0))).append(" and ").append(padSqlArg(args.get(1)));
                //sb.append(" (").append(padSqlArg(args.get(0))).append(",").append(padSqlArg(args.get(1))).append(") ");
                break;
            case IN:
                sb.append(" (").append(padSqlArgs(args)).append(")");
                break;
            default:
                throw new InternalAdapterException("Unknown condition applied. Value found was " + condition + " and is not (yet) supported. Please contact BlobCity Tech Support for more details.");
        }

        if (!operators.isEmpty()) {
            final int operatorCount = operators.size();
            final int conditionCount = conditions.size();
            for (int i = 0; i < operatorCount && i < conditionCount; i++) {
                sb.append(" ").append(operators.get(i)).append(" ").append(conditions.get(i).asSql());
            }
        }

        return sb.toString();
    }

    /**
     * Method is internally called whenever the {@link #condition} and/or {@link #args} are updated.
     *
     * @see #in(java.lang.Object[])
     * @see #eq(java.lang.Object)
     * @see #noteq(java.lang.Object)
     * @see #lt(java.lang.Object)
     * @see #gt(java.lang.Object)
     * @see #lteq(java.lang.Object)
     * @see #gteq(java.lang.Object)
     * @see #between(java.lang.Object, java.lang.Object)
     * @return current instance of {@link SearchParam}
     */
    private SearchParam updateBaseParams() {
        baseParamMap.put("x", condition);
        baseParamMap.put("v", padJsonArgs());
        return this;
    }

    /**
     * Pads an argument for a JSON query
     *
     * @return if the operator only requires a single element, that element is returned, else a {@link JSONArray} is returned for the same.
     */
    private Object padJsonArgs() {
        switch (condition) {
            case EQ:
            case NOT_EQ:
            case LT:
            case LT_EQ:
            case GT:
            case GT_EQ:
                return args.get(0);
            case BETWEEN:
            case IN:
                return args;
            default:
                throw new InternalAdapterException("Unknown condition applied. Value found was " + condition + " and is not (yet) supported. Please contact BlobCity Tech Support for more details.");
        }
    }

    /**
     * Pads arguments for SQL by quoting them as per SQL spec. Internally uses {@link #padSqlArg(java.lang.Object)}
     *
     * @see #padSqlArg(java.lang.Object)
     * @param jsonArr Array of objects to be escaped
     * @return SQL compliant form for the arguments
     * @throws JSONException th
     */
    private String padSqlArgs(final JsonArray jsonArr) {
        final StringBuffer sb = new StringBuffer();
        final int length = jsonArr.size();
        for (int i = 0; i < length; i++) {
            sb.append(padSqlArg(jsonArr.get(i)));
            if (i < length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    /**
     * Pads an argument for an SQL query's WHERE clause as required by the SQL spec.
     *
     * @param obj Object to the quote escaped (if required)
     * @return SQL compliant form of the argument ready for consumption by a query
     */
    private String padSqlArg(final JsonElement obj) {
        if (obj.getAsJsonPrimitive().isString()) { // Strings and chars
            return "'" + obj.getAsJsonPrimitive().getAsString() + "'";
        }

        return obj.getAsString();
    }

    private void addArgs(final Object... objs) {
        args = new JsonArray();
        for (final Object obj : objs) {
            args.add(getJsonPrimitive(obj));
        }
    }

    private JsonPrimitive getJsonPrimitive(final Object obj) {
        final Class clazz = obj.getClass();

        if (obj instanceof String) {
            return new JsonPrimitive((String) obj);
        }

        if (clazz == int.class || clazz == Integer.class) {
            return new JsonPrimitive((Integer) obj);
        }

        if (clazz == long.class || clazz == Long.class) {
            return new JsonPrimitive((Long) obj);
        }

        if (clazz == short.class || clazz == Short.class) {
            return new JsonPrimitive((Short) obj);
        }

        if (clazz == float.class || clazz == Float.class) {
            return new JsonPrimitive((Float) obj);
        }

        if (clazz == double.class || clazz == Double.class) {
            return new JsonPrimitive((Double) obj);
        }

        if (clazz == byte.class || clazz == Byte.class) {
            return new JsonPrimitive((Byte) obj);
        }

        if (clazz == boolean.class || clazz == Boolean.class) {
            return new JsonPrimitive((Boolean) obj);
        }

        if (clazz == char.class || clazz == Character.class) {
            return new JsonPrimitive((Character) obj);
        }

        return new JsonPrimitive(obj.toString());
    }
}
