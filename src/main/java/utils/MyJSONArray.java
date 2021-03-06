package utils;

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.json.*;

/**
 * A MyJSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>MyJSONArray</code>, <code>MyJSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>MyJSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is <code>,</code>
 * &nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2013-04-18
 */
public class MyJSONArray {

    /**
     * The arrayList where the MyJSONArray's properties are kept.
     */
    private final ArrayList myArrayList;

    /**
     * Construct an empty MyJSONArray.
     */
    public MyJSONArray() {
        this.myArrayList = new ArrayList();
    }

    /**
     * Construct a MyJSONArray from a JSONTokener.
     *
     * @param x
     *            A JSONTokener
     * @throws JSONException
     *             If there is a syntax error.
     */
    public MyJSONArray(JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A MyJSONArray text must start with '['");
        }
        if (x.nextClean() != ']') {
            x.back();
            for (;;) {
                if (x.nextClean() == ',') {
                    x.back();
                    this.myArrayList.add(MyJSONObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                    case ',':
                        if (x.nextClean() == ']') {
                            return;
                        }
                        x.back();
                        break;
                    case ']':
                        return;
                    default:
                        throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }

    /**
     * Construct a MyJSONArray from a source JSON text.
     *
     * @param source
     *            A string that begins with <code>[</code>&nbsp;<small>(left
     *            bracket)</small> and ends with <code>]</code>
     *            &nbsp;<small>(right bracket)</small>.
     * @throws JSONException
     *             If there is a syntax error.
     */
    public MyJSONArray(String source) throws JSONException {
        this(new JSONTokener(source));
    }

    /**
     * Construct a MyJSONArray from a Collection.
     *
     * @param collection
     *            A Collection.
     */
    public MyJSONArray(Collection collection) {
        this.myArrayList = new ArrayList();
        if (collection != null) {
            Iterator iter = collection.iterator();
            while (iter.hasNext()) {
                this.myArrayList.add(MyJSONObject.wrap(iter.next()));
            }
        }
    }

    /**
     * Construct a MyJSONArray from an array
     *
     * @throws JSONException
     *             If not an array.
     */
    public MyJSONArray(Object array) throws JSONException {
        this();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i += 1) {
                this.put(MyJSONObject.wrap(Array.get(array, i)));
            }
        } else {
            throw new JSONException(
                    "MyJSONArray initial value should be a string or collection or array.");
        }
    }

    /**
     * Get the object value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JSONException
     *             If there is no value for the index.
     */
    public Object get(int index) throws JSONException {
        Object object = this.opt(index);
        if (object == null) {
            throw new JSONException("MyJSONArray[" + index + "] not found.");
        }
        return object;
    }

    /**
     * Get the boolean value associated with an index. The string values "true"
     * and "false" are converted to boolean.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JSONException
     *             If there is no value for the index or if the value is not
     *             convertible to boolean.
     */
    public boolean getBoolean(int index) throws JSONException {
        Object object = this.get(index);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                .equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("MyJSONArray[" + index + "] is not a boolean.");
    }

    /**
     * Get the double value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public double getDouble(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw new JSONException("MyJSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value is not a number.
     */
    public int getInt(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw new JSONException("MyJSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the MyJSONArray associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A MyJSONArray value.
     * @throws JSONException
     *             If there is no value for the index. or if the value is not a
     *             MyJSONArray
     */
    public MyJSONArray getJSONArray(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof MyJSONArray) {
            return (MyJSONArray) object;
        }
        throw new JSONException("MyJSONArray[" + index + "] is not a MyJSONArray.");
    }

    /**
     * Get the MyJSONObject associated with an index.
     *
     * @param index
     *            subscript
     * @return A MyJSONObject value.
     * @throws JSONException
     *             If there is no value for the index or if the value is not a
     *             MyJSONObject
     */
    public MyJSONObject getJSONObject(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof MyJSONObject) {
            return (MyJSONObject) object;
        }
        throw new JSONException("MyJSONArray[" + index + "] is not a MyJSONObject.");
    }

    /**
     * Get the long value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public long getLong(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw new JSONException("MyJSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the string associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JSONException
     *             If there is no string value for the index.
     */
    public String getString(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw new JSONException("MyJSONArray[" + index + "] not a string.");
    }

    /**
     * Determine if the value is null.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) {
        return MyJSONObject.NULL.equals(this.opt(index));
    }

    /**
     * Make a string from the contents of this MyJSONArray. The
     * <code>separator</code> string is inserted between each element. Warning:
     * This method assumes that the data structure is acyclical.
     *
     * @param separator
     *            A string that will be inserted between the elements.
     * @return a string.
     * @throws JSONException
     *             If the array contains an invalid number.
     */
    public String join(String separator) throws JSONException {
        int len = this.length();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(MyJSONObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }

    /**
     * Get the number of elements in the MyJSONArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() {
        return this.myArrayList.size();
    }

    /**
     * Get the optional object value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return An object value, or null if there is no object at that index.
     */
    public Object opt(int index) {
        return (index < 0 || index >= this.length()) ? null : this.myArrayList
                .get(index);
    }

    /**
     * Get the optional boolean value associated with an index. It returns false
     * if there is no value at that index, or if the value is not Boolean.TRUE
     * or the String "true".
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The truth.
     */
    public boolean optBoolean(int index) {
        return this.optBoolean(index, false);
    }

    /**
     * Get the optional boolean value associated with an index. It returns the
     * defaultValue if there is no value at that index or if it is not a Boolean
     * or the String "true" or "false" (case insensitive).
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @param defaultValue
     *            A boolean default.
     * @return The truth.
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return this.getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional double value associated with an index. NaN is returned
     * if there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     */
    public double optDouble(int index) {
        return this.optDouble(index, Double.NaN);
    }

    /**
     * Get the optional double value associated with an index. The defaultValue
     * is returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param index
     *            subscript
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public double optDouble(int index, double defaultValue) {
        try {
            return this.getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional int value associated with an index. Zero is returned if
     * there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     */
    public int optInt(int index) {
        return this.optInt(index, 0);
    }

    /**
     * Get the optional int value associated with an index. The defaultValue is
     * returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public int optInt(int index, int defaultValue) {
        try {
            return this.getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional MyJSONArray associated with an index.
     *
     * @param index
     *            subscript
     * @return A MyJSONArray value, or null if the index has no value, or if the
     *         value is not a MyJSONArray.
     */
    public MyJSONArray optJSONArray(int index) {
        Object o = this.opt(index);
        return o instanceof MyJSONArray ? (MyJSONArray) o : null;
    }

    /**
     * Get the optional MyJSONObject associated with an index. Null is returned if
     * the key is not found, or null if the index has no value, or if the value
     * is not a MyJSONObject.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A MyJSONObject value.
     */
    public MyJSONObject optJSONObject(int index) {
        Object o = this.opt(index);
        return o instanceof MyJSONObject ? (MyJSONObject) o : null;
    }

    /**
     * Get the optional long value associated with an index. Zero is returned if
     * there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     */
    public long optLong(int index) {
        return this.optLong(index, 0);
    }

    /**
     * Get the optional long value associated with an index. The defaultValue is
     * returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @param defaultValue
     *            The default value.
     * @return The value.
     */
    public long optLong(int index, long defaultValue) {
        try {
            return this.getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get the optional string value associated with an index. It returns an
     * empty string if there is no value at that index. If the value is not a
     * string and is not null, then it is coverted to a string.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A String value.
     */
    public String optString(int index) {
        return this.optString(index, "");
    }

    /**
     * Get the optional string associated with an index. The defaultValue is
     * returned if the key is not found.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @param defaultValue
     *            The default value.
     * @return A String value.
     */
    public String optString(int index, String defaultValue) {
        Object object = this.opt(index);
        return MyJSONObject.NULL.equals(object) ? defaultValue : object
                .toString();
    }

    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value
     *            A boolean value.
     * @return this.
     */
    public MyJSONArray put(boolean value) {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put a value in the MyJSONArray, where the value will be a MyJSONArray which
     * is produced from a Collection.
     *
     * @param value
     *            A Collection value.
     * @return this.
     */
    public MyJSONArray put(Collection value) {
        this.put(new MyJSONArray(value));
        return this;
    }

    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value
     *            A double value.
     * @throws JSONException
     *             if the value is not finite.
     * @return this.
     */
    public MyJSONArray put(double value) throws JSONException {
        Double d = new Double(value);
        MyJSONObject.testValidity(d);
        this.put(d);
        return this;
    }

    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value
     *            An int value.
     * @return this.
     */
    public MyJSONArray put(int value) {
        this.put(new Integer(value));
        return this;
    }

    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value
     *            A long value.
     * @return this.
     */
    public MyJSONArray put(long value) {
        this.put(new Long(value));
        return this;
    }

    /**
     * Put a value in the MyJSONArray, where the value will be a MyJSONObject which
     * is produced from a Map.
     *
     * @param value
     *            A Map value.
     * @return this.
     */
    public MyJSONArray put(Map value) {
        this.put(new MyJSONObject(value));
        return this;
    }

    /**
     * Append an object value. This increases the array's length by one.
     *
     * @param value
     *            An object value. The value should be a Boolean, Double,
     *            Integer, MyJSONArray, MyJSONObject, Long, or String, or the
     *            MyJSONObject.NULL object.
     * @return this.
     */
    public MyJSONArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    /**
     * Put or replace a boolean value in the MyJSONArray. If the index is greater
     * than the length of the MyJSONArray, then null elements will be added as
     * necessary to pad it out.
     *
     * @param index
     *            The subscript.
     * @param value
     *            A boolean value.
     * @return this.
     * @throws JSONException
     *             If the index is negative.
     */
    public MyJSONArray put(int index, boolean value) throws JSONException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Put a value in the MyJSONArray, where the value will be a MyJSONArray which
     * is produced from a Collection.
     *
     * @param index
     *            The subscript.
     * @param value
     *            A Collection value.
     * @return this.
     * @throws JSONException
     *             If the index is negative or if the value is not finite.
     */
    public MyJSONArray put(int index, Collection value) throws JSONException {
        this.put(index, new MyJSONArray(value));
        return this;
    }

    /**
     * Put or replace a double value. If the index is greater than the length of
     * the MyJSONArray, then null elements will be added as necessary to pad it
     * out.
     *
     * @param index
     *            The subscript.
     * @param value
     *            A double value.
     * @return this.
     * @throws JSONException
     *             If the index is negative or if the value is not finite.
     */
    public MyJSONArray put(int index, double value) throws JSONException {
        this.put(index, new Double(value));
        return this;
    }

    /**
     * Put or replace an int value. If the index is greater than the length of
     * the MyJSONArray, then null elements will be added as necessary to pad it
     * out.
     *
     * @param index
     *            The subscript.
     * @param value
     *            An int value.
     * @return this.
     * @throws JSONException
     *             If the index is negative.
     */
    public MyJSONArray put(int index, int value) throws JSONException {
        this.put(index, new Integer(value));
        return this;
    }

    /**
     * Put or replace a long value. If the index is greater than the length of
     * the MyJSONArray, then null elements will be added as necessary to pad it
     * out.
     *
     * @param index
     *            The subscript.
     * @param value
     *            A long value.
     * @return this.
     * @throws JSONException
     *             If the index is negative.
     */
    public MyJSONArray put(int index, long value) throws JSONException {
        this.put(index, new Long(value));
        return this;
    }

    /**
     * Put a value in the MyJSONArray, where the value will be a MyJSONObject that
     * is produced from a Map.
     *
     * @param index
     *            The subscript.
     * @param value
     *            The Map value.
     * @return this.
     * @throws JSONException
     *             If the index is negative or if the the value is an invalid
     *             number.
     */
    public MyJSONArray put(int index, Map value) throws JSONException {
        this.put(index, new MyJSONObject(value));
        return this;
    }

    /**
     * Put or replace an object value in the MyJSONArray. If the index is greater
     * than the length of the MyJSONArray, then null elements will be added as
     * necessary to pad it out.
     *
     * @param index
     *            The subscript.
     * @param value
     *            The value to put into the array. The value should be a
     *            Boolean, Double, Integer, MyJSONArray, MyJSONObject, Long, or
     *            String, or the MyJSONObject.NULL object.
     * @return this.
     * @throws JSONException
     *             If the index is negative or if the the value is an invalid
     *             number.
     */
    public MyJSONArray put(int index, Object value) throws JSONException {
        MyJSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("MyJSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value);
        } else {
            while (index != this.length()) {
                this.put(MyJSONObject.NULL);
            }
            this.put(value);
        }
        return this;
    }

    /**
     * Remove an index and close the hole.
     *
     * @param index
     *            The index of the element to be removed.
     * @return The value that was associated with the index, or null if there
     *         was no value.
     */
    public Object remove(int index) {
        Object o = this.opt(index);
        this.myArrayList.remove(index);
        return o;
    }

    /**
     * Produce a MyJSONObject by combining a MyJSONArray of names with the values of
     * this MyJSONArray.
     *
     * @param names
     *            A MyJSONArray containing a list of key strings. These will be
     *            paired with the values.
     * @return A MyJSONObject, or null if there are no names or if this MyJSONArray
     *         has no values.
     * @throws JSONException
     *             If any of the names are null.
     */
    public MyJSONObject toJSONObject(MyJSONArray names) throws JSONException {
        if (names == null || names.length() == 0 || this.length() == 0) {
            return null;
        }
        MyJSONObject jo = new MyJSONObject();
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }

    /**
     * Make a JSON text of this MyJSONArray. For compactness, no unnecessary
     * whitespace is added. If it is not possible to produce a syntactically
     * correct JSON text then null will be returned instead. This could occur if
     * the array contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable representation of the
     *         array.
     */
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a prettyprinted JSON text of this MyJSONArray. Warning: This method
     * assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @return a printable, displayable, transmittable representation of the
     *         object, beginning with <code>[</code>&nbsp;<small>(left
     *         bracket)</small> and ending with <code>]</code>
     *         &nbsp;<small>(right bracket)</small>.
     * @throws JSONException
     */
    public String toString(int indentFactor) throws JSONException {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    /**
     * Write the contents of the MyJSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JSONException
     */
    public Writer write(Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }

    /**
     * Write the contents of the MyJSONArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     *            The number of spaces to add to each level of indentation.
     * @param indent
     *            The indention of the top level.
     * @return The writer.
     * @throws JSONException
     */
    Writer write(Writer writer, int indentFactor, int indent)
            throws JSONException {
        try {
            boolean commanate = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                MyJSONObject.writeValue(writer, this.myArrayList.get(0),
                        indentFactor, indent);
            } else if (length != 0) {
                final int newindent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (commanate) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    MyJSONObject.indent(writer, newindent);
                    MyJSONObject.writeValue(writer, this.myArrayList.get(i),
                            indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                MyJSONObject.indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JSONException(e);
        }
    }
}
