/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.datasecure.sensitive;



import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 */
public abstract class JToStringStyle implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long                                     serialVersionUID = 1321586356071032683L;

    /**
     * The JSON toString style. Using the <code>Person</code> web from
     * {@link ToStringBuilder}, the output would look like this:
     *
     * <pre>
     * {"name": "John Doe", "age": 33, "smoker": true}
     * </pre>
     *
     * <strong>Note:</strong> Since field names are mandatory in JSON, this
     * ToStringStyle will throw an {@link UnsupportedOperationException} if no
     * field name is passed in while appending. Furthermore This ToStringStyle
     * will only generate valid JSON if referenced objects also produce JSON
     * when calling {@code toString()} on them.
     *
     * @since 3.4
     * @see <a href="http://json.org">json.org</a>
     */
    public static final ToStringStyle                             JSON_STYLE       = new JsonToStringStyle();

    /**
     * <p>
     * A registry of objects used by <code>reflectionToString</code> methods to
     * detect cyclical object references and avoid infinite loops.
     * </p>
     */
    private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY         = new ThreadLocal<WeakHashMap<Object, Object>>();
    /*
     * Note that objects of this class are generally shared between threads, so
     * an instance variable would not be suitable here. In normal use the
     * registry should always be left empty, because the caller should call
     * toString() which will clean up. See LANG-792
     */

    /**
     * <p>
     * Returns the registry of objects being traversed by the
     * <code>reflectionToString</code> methods in the current thread.
     * </p>
     *
     * @return Set the registry of objects being traversed
     */
    static Map<Object, Object> getRegistry() {
        return REGISTRY.get();
    }

    /**
     * <p>
     * Returns <code>true</code> if the registry contains the given object. Used
     * by the reflection methods to avoid infinite loops.
     * </p>
     *
     * @param value The object to lookup in the registry.
     * @return boolean <code>true</code> if the registry contains the given
     *         object.
     */
    static boolean isRegistered(final Object value) {
        final Map<Object, Object> m = getRegistry();
        return m != null && m.containsKey(value);
    }

    /**
     * <p>
     * Registers the given object. Used by the reflection methods to avoid
     * infinite loops.
     * </p>
     *
     * @param value The object to register.
     */
    static void register(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m == null) {
                REGISTRY.set(new WeakHashMap<Object, Object>());
            }
            getRegistry().put(value, null);
        }
    }

    /**
     * <p>
     * Unregisters the given object.
     * </p>
     * <p>
     * Used by the reflection methods to avoid infinite loops.
     * </p>
     *
     * @param value The object to unregister.
     */
    static void unregister(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m != null) {
                m.remove(value);
                if (m.isEmpty()) {
                    REGISTRY.remove();
                }
            }
        }
    }

    /**
     * Whether to use the field names, the default is <code>true</code>.
     */
    private boolean useFieldNames           = true;

    /**
     * Whether to use the class name, the default is <code>true</code>.
     */
    private boolean useClassName            = true;

    /**
     * Whether to use short class names, the default is <code>false</code>.
     */
    private boolean useShortClassName       = false;

    /**
     * Whether to use the identity hash code, the default is <code>true</code>.
     */
    private boolean useIdentityHashCode     = true;

    /**
     * The content start <code>'['</code>.
     */
    private String  contentStart            = "[";

    /**
     * The content end <code>']'</code>.
     */
    private String  contentEnd              = "]";

    /**
     * The field name value separator <code>'='</code>.
     */
    private String  fieldNameValueSeparator = "=";

    /**
     * Whether the field separator should be added before any other fields.
     */
    private boolean fieldSeparatorAtStart   = false;

    /**
     * Whether the field separator should be added after any other fields.
     */
    private boolean fieldSeparatorAtEnd     = false;

    /**
     * The field separator <code>','</code>.
     */
    private String  fieldSeparator          = ",";

    /**
     * The array start <code>'{'</code>.
     */
    private String  arrayStart              = "{";

    /**
     * The array separator <code>','</code>.
     */
    private String  arraySeparator          = ",";

    /**
     * The detail for array content.
     */
    private boolean arrayContentDetail      = true;

    /**
     * The array end <code>'}'</code>.
     */
    private String  arrayEnd                = "}";

    /**
     * The value to use when fullDetail is <code>null</code>, the default value
     * is <code>true</code>.
     */
    private boolean defaultFullDetail       = true;

    /**
     * The <code>null</code> text <code>'&lt;null&gt;'</code>.
     */
    private String  nullText                = "<null>";

    /**
     * The summary size text start <code>'&lt;size'</code>.
     */
    private String  sizeStartText           = "<size=";

    /**
     * The summary size text start <code>'&gt;'</code>.
     */
    private String  sizeEndText             = ">";

    /**
     * The summary object text start <code>'&lt;'</code>.
     */
    private String  summaryObjectStartText  = "<";

    /**
     * The summary object text start <code>'&gt;'</code>.
     */
    private String  summaryObjectEndText    = ">";

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Constructor.
     * </p>
     */
    protected JToStringStyle() {
        super();
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> the superclass toString.
     * </p>
     * <p>
     * NOTE: It assumes that the toString has been created from the same
     * ToStringStyle.
     * </p>
     * <p>
     * A <code>null</code> <code>superToString</code> is ignored.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param superToString the <code>super.toString()</code>
     * @since 2.0
     */
    public void appendSuper(final StringBuffer buffer, final String superToString) {
        this.appendToString(buffer, superToString);
    }

    /**
     * <p>
     * Append to the <code>toString</code> another toString.
     * </p>
     * <p>
     * NOTE: It assumes that the toString has been created from the same
     * ToStringStyle.
     * </p>
     * <p>
     * A <code>null</code> <code>toString</code> is ignored.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param toString the additional <code>toString</code>
     * @since 2.0
     */
    public void appendToString(final StringBuffer buffer, final String toString) {
        if (toString != null) {
            final int pos1 = toString.indexOf(this.contentStart) + this.contentStart.length();
            final int pos2 = toString.lastIndexOf(this.contentEnd);
            if (pos1 != pos2 && pos1 >= 0 && pos2 >= 0) {
                final String data = toString.substring(pos1, pos2);
                if (this.fieldSeparatorAtStart) {
                    this.removeLastFieldSeparator(buffer);
                }
                buffer.append(data);
                this.appendFieldSeparator(buffer);
            }
        }
    }

    /**
     * <p>
     * Append to the <code>toString</code> the start of data indicator.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param object the <code>Object</code> to build a <code>toString</code>
     *            for
     */
    public void appendStart(final StringBuffer buffer, final Object object) {
        if (object != null) {
            this.appendClassName(buffer, object);
            this.appendIdentityHashCode(buffer, object);
            this.appendContentStart(buffer);
            if (this.fieldSeparatorAtStart) {
                this.appendFieldSeparator(buffer);
            }
        }
    }

    /**
     * <p>
     * Append to the <code>toString</code> the end of data indicator.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param object the <code>Object</code> to build a <code>toString</code>
     *            for.
     */
    public void appendEnd(final StringBuffer buffer, final Object object) {
        if (this.fieldSeparatorAtEnd == false) {
            this.removeLastFieldSeparator(buffer);
        }
        this.appendContentEnd(buffer);
        unregister(object);
    }

    /**
     * <p>
     * Remove the last field separator from the buffer.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @since 2.0
     */
    protected void removeLastFieldSeparator(final StringBuffer buffer) {
        final int len = buffer.length();
        final int sepLen = this.fieldSeparator.length();
        if (len > 0 && sepLen > 0 && len >= sepLen) {
            boolean match = true;
            for (int i = 0; i < sepLen; i++) {
                if (buffer.charAt(len - 1 - i) != this.fieldSeparator.charAt(sepLen - 1 - i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                buffer.setLength(len - sepLen);
            }
        }
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code> value,
     * printing the full <code>toString</code> of the <code>Object</code> passed
     * in.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final Object value,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (value == null) {
            this.appendNullText(buffer, fieldName);

        } else {
            this.appendInternal(buffer, fieldName, value, this.isFullDetail(fullDetail));
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code>, correctly
     * interpreting its type.
     * </p>
     * <p>
     * This method performs the main lookup by Class type to correctly route
     * arrays, <code>Collections</code>, <code>Maps</code> and
     * <code>Objects</code> to the appropriate method.
     * </p>
     * <p>
     * Either detail or summary views can be specified.
     * </p>
     * <p>
     * If a cycle is detected, an object will be appended with the
     * <code>Object.toString()</code> format.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>, not
     *            <code>null</code>
     * @param detail output detail or not
     */
    protected void appendInternal(final StringBuffer buffer, final String fieldName, final Object value,
                                  final boolean detail) {
        if (isRegistered(value)
                && !(value instanceof Number || value instanceof Boolean || value instanceof Character)) {
            this.appendCyclicObject(buffer, fieldName, value);
            return;
        }

        register(value);

        try {
            if (value instanceof Collection<?>) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Collection<?>) value);
                } else {
                    this.appendSummarySize(buffer, fieldName, ((Collection<?>) value).size());
                }

            } else if (value instanceof Map<?, ?>) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Map<?, ?>) value);
                } else {
                    this.appendSummarySize(buffer, fieldName, ((Map<?, ?>) value).size());
                }

            } else if (value instanceof long[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (long[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (long[]) value);
                }

            } else if (value instanceof int[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (int[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (int[]) value);
                }

            } else if (value instanceof short[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (short[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (short[]) value);
                }

            } else if (value instanceof byte[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (byte[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (byte[]) value);
                }

            } else if (value instanceof char[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (char[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (char[]) value);
                }

            } else if (value instanceof double[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (double[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (double[]) value);
                }

            } else if (value instanceof float[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (float[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (float[]) value);
                }

            } else if (value instanceof boolean[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (boolean[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (boolean[]) value);
                }

            } else if (value.getClass().isArray()) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Object[]) value);
                } else {
                    this.appendSummary(buffer, fieldName, (Object[]) value);
                }

            } else {
                if (detail) {
                    this.appendDetail(buffer, fieldName, value);
                } else {
                    this.appendSummary(buffer, fieldName, value);
                }
            }
        } finally {
            unregister(value);
        }
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code> value that has
     * been detected to participate in a cycle. This implementation will print
     * the standard string value of the value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>, not
     *            <code>null</code>
     * @since 2.2
     */
    protected void appendCyclicObject(final StringBuffer buffer, final String fieldName, final Object value) {
        ObjectUtils.identityToString(buffer, value);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code> value,
     * printing the full detail of the <code>Object</code>.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(value);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>Collection</code>.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param coll the <code>Collection</code> to add to the
     *            <code>toString</code>, not <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {
        buffer.append(coll);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>Map</code>.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param map the <code>Map</code> to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Map<?, ?> map) {
        buffer.append(map);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code> value,
     * printing a summary of the <code>Object</code>.
     * </P>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(this.summaryObjectStartText);
        buffer.append(this.getShortClassName(value.getClass()));
        buffer.append(this.summaryObjectEndText);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>long</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final long value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>long</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> an <code>int</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final int value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>int</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>short</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final short value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>short</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>byte</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final byte value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>byte</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>char</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final char value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>char</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>double</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final double value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>double</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>float</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final float value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>float</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float value) {
        buffer.append(value);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>boolean</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param value the value to add to the <code>toString</code>
     */
    public void append(final StringBuffer buffer, final String fieldName, final boolean value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a <code>boolean</code> value.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param value the value to add to the <code>toString</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean value) {
        buffer.append(value);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an <code>Object</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the toString
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final Object[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> the detail of an <code>Object</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            final Object item = array[i];
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                this.appendNullText(buffer, fieldName);

            } else {
                this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of an array type.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     * @since 2.0
     */
    protected void reflectionAppendArrayDetail(final StringBuffer buffer, final String fieldName, final Object array) {
        buffer.append(this.arrayStart);
        final int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            final Object item = Array.get(array, i);
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                this.appendNullText(buffer, fieldName);

            } else {
                this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of an <code>Object</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>long</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final long[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>long</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>long</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final long[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> an <code>int</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final int[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of an <code>int</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of an <code>int</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final int[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>short</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final short[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>short</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>short</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final short[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>byte</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final byte[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>byte</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>byte</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final byte[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>char</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the <code>toString</code>
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final char[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>char</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>char</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final char[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>double</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the toString
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final double[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>double</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>double</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final double[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>float</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the toString
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final float[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>float</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>float</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final float[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> a <code>boolean</code> array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     * @param array the array to add to the toString
     * @param fullDetail <code>true</code> for detail, <code>false</code> for
     *            summary info, <code>null</code> for style decides
     */
    public void append(final StringBuffer buffer, final String fieldName, final boolean[] array,
                       final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);

        if (array == null) {
            this.appendNullText(buffer, fieldName);

        } else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);

        } else {
            this.appendSummary(buffer, fieldName, array);
        }

        this.appendFieldEnd(buffer, fieldName);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the detail of a <code>boolean</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a summary of a <code>boolean</code>
     * array.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param array the array to add to the <code>toString</code>, not
     *            <code>null</code>
     */
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }

    //----------------------------------------------------------------------------

    /**
     * <p>
     * Append to the <code>toString</code> the class name.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param object the <code>Object</code> whose name to output
     */
    protected void appendClassName(final StringBuffer buffer, final Object object) {
        if (this.useClassName && object != null) {
            register(object);
            if (this.useShortClassName) {
                buffer.append(this.getShortClassName(object.getClass()));
            } else {
                buffer.append(object.getClass().getName());
            }
        }
    }

    /**
     * <p>
     * Append the {@link System#identityHashCode(java.lang.Object)}.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param object the <code>Object</code> whose id to output
     */
    protected void appendIdentityHashCode(final StringBuffer buffer, final Object object) {
        if (this.isUseIdentityHashCode() && object != null) {
            register(object);
            buffer.append('@');
            buffer.append(Integer.toHexString(System.identityHashCode(object)));
        }
    }

    /**
     * <p>
     * Append to the <code>toString</code> the content start.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     */
    protected void appendContentStart(final StringBuffer buffer) {
        buffer.append(this.contentStart);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the content end.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     */
    protected void appendContentEnd(final StringBuffer buffer) {
        buffer.append(this.contentEnd);
    }

    /**
     * <p>
     * Append to the <code>toString</code> an indicator for <code>null</code>.
     * </p>
     * <p>
     * The default indicator is <code>'&lt;null&gt;'</code>.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     */
    protected void appendNullText(final StringBuffer buffer, final String fieldName) {
        buffer.append(this.nullText);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the field separator.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     */
    protected void appendFieldSeparator(final StringBuffer buffer) {
        buffer.append(this.fieldSeparator);
    }

    /**
     * <p>
     * Append to the <code>toString</code> the field start.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name
     */
    protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
        if (this.useFieldNames && fieldName != null) {
            buffer.append(fieldName);
            buffer.append(this.fieldNameValueSeparator);
        }
    }

    /**
     * <p>
     * Append to the <code>toString</code> the field end.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     */
    protected void appendFieldEnd(final StringBuffer buffer, final String fieldName) {
        this.appendFieldSeparator(buffer);
    }

    /**
     * <p>
     * Append to the <code>toString</code> a size summary.
     * </p>
     * <p>
     * The size summary is used to summarize the contents of
     * <code>Collections</code>, <code>Maps</code> and arrays.
     * </p>
     * <p>
     * The output consists of a prefix, the passed in size and a suffix.
     * </p>
     * <p>
     * The default format is <code>'&lt;size=n&gt;'</code>.
     * </p>
     *
     * @param buffer the <code>StringBuffer</code> to populate
     * @param fieldName the field name, typically not used as already appended
     * @param size the size to append
     */
    protected void appendSummarySize(final StringBuffer buffer, final String fieldName, final int size) {
        buffer.append(this.sizeStartText);
        buffer.append(size);
        buffer.append(this.sizeEndText);
    }

    /**
     * <p>
     * Is this field to be output in full detail.
     * </p>
     * <p>
     * This method converts a detail request into a detail level. The calling
     * code may request full detail (<code>true</code>), but a subclass might
     * ignore that and always return <code>false</code>. The calling code may
     * pass in <code>null</code> indicating that it doesn't care about the
     * detail level. In this case the default detail level is used.
     * </p>
     *
     * @param fullDetailRequest the detail level requested
     * @return whether full detail is to be shown
     */
    protected boolean isFullDetail(final Boolean fullDetailRequest) {
        if (fullDetailRequest == null) {
            return this.defaultFullDetail;
        }
        return fullDetailRequest.booleanValue();
    }

    /**
     * <p>
     * Gets the short class name for a class.
     * </p>
     * <p>
     * The short class name is the classname excluding the package name.
     * </p>
     *
     * @param cls the <code>Class</code> to get the short name of
     * @return the short name
     */
    protected String getShortClassName(final Class<?> cls) {
        return ClassUtils.getShortClassName(cls);
    }

    // Setters and getters for the customizable parts of the style
    // These methods are not expected to be overridden, except to make public
    // (They are not public so that immutable subclasses can be written)
    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to use the class name.
     * </p>
     *
     * @return the current useClassName flag
     */
    protected boolean isUseClassName() {
        return this.useClassName;
    }

    /**
     * <p>
     * Sets whether to use the class name.
     * </p>
     *
     * @param useClassName the new useClassName flag
     */
    protected void setUseClassName(final boolean useClassName) {
        this.useClassName = useClassName;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to output short or long class names.
     * </p>
     *
     * @return the current useShortClassName flag
     * @since 2.0
     */
    protected boolean isUseShortClassName() {
        return this.useShortClassName;
    }

    /**
     * <p>
     * Sets whether to output short or long class names.
     * </p>
     *
     * @param useShortClassName the new useShortClassName flag
     * @since 2.0
     */
    protected void setUseShortClassName(final boolean useShortClassName) {
        this.useShortClassName = useShortClassName;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to use the identity hash code.
     * </p>
     *
     * @return the current useIdentityHashCode flag
     */
    protected boolean isUseIdentityHashCode() {
        return this.useIdentityHashCode;
    }

    /**
     * <p>
     * Sets whether to use the identity hash code.
     * </p>
     *
     * @param useIdentityHashCode the new useIdentityHashCode flag
     */
    protected void setUseIdentityHashCode(final boolean useIdentityHashCode) {
        this.useIdentityHashCode = useIdentityHashCode;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to use the field names passed in.
     * </p>
     *
     * @return the current useFieldNames flag
     */
    protected boolean isUseFieldNames() {
        return this.useFieldNames;
    }

    /**
     * <p>
     * Sets whether to use the field names passed in.
     * </p>
     *
     * @param useFieldNames the new useFieldNames flag
     */
    protected void setUseFieldNames(final boolean useFieldNames) {
        this.useFieldNames = useFieldNames;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to use full detail when the caller doesn't specify.
     * </p>
     *
     * @return the current defaultFullDetail flag
     */
    protected boolean isDefaultFullDetail() {
        return this.defaultFullDetail;
    }

    /**
     * <p>
     * Sets whether to use full detail when the caller doesn't specify.
     * </p>
     *
     * @param defaultFullDetail the new defaultFullDetail flag
     */
    protected void setDefaultFullDetail(final boolean defaultFullDetail) {
        this.defaultFullDetail = defaultFullDetail;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether to output array content detail.
     * </p>
     *
     * @return the current array content detail setting
     */
    protected boolean isArrayContentDetail() {
        return this.arrayContentDetail;
    }

    /**
     * <p>
     * Sets whether to output array content detail.
     * </p>
     *
     * @param arrayContentDetail the new arrayContentDetail flag
     */
    protected void setArrayContentDetail(final boolean arrayContentDetail) {
        this.arrayContentDetail = arrayContentDetail;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the array start text.
     * </p>
     *
     * @return the current array start text
     */
    protected String getArrayStart() {
        return this.arrayStart;
    }

    /**
     * <p>
     * Sets the array start text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param arrayStart the new array start text
     */
    protected void setArrayStart(String arrayStart) {
        if (arrayStart == null) {
            arrayStart = StringUtils.EMPTY;
        }
        this.arrayStart = arrayStart;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the array end text.
     * </p>
     *
     * @return the current array end text
     */
    protected String getArrayEnd() {
        return this.arrayEnd;
    }

    /**
     * <p>
     * Sets the array end text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param arrayEnd the new array end text
     */
    protected void setArrayEnd(String arrayEnd) {
        if (arrayEnd == null) {
            arrayEnd = StringUtils.EMPTY;
        }
        this.arrayEnd = arrayEnd;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the array separator text.
     * </p>
     *
     * @return the current array separator text
     */
    protected String getArraySeparator() {
        return this.arraySeparator;
    }

    /**
     * <p>
     * Sets the array separator text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param arraySeparator the new array separator text
     */
    protected void setArraySeparator(String arraySeparator) {
        if (arraySeparator == null) {
            arraySeparator = StringUtils.EMPTY;
        }
        this.arraySeparator = arraySeparator;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the content start text.
     * </p>
     *
     * @return the current content start text
     */
    protected String getContentStart() {
        return this.contentStart;
    }

    /**
     * <p>
     * Sets the content start text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param contentStart the new content start text
     */
    protected void setContentStart(String contentStart) {
        if (contentStart == null) {
            contentStart = StringUtils.EMPTY;
        }
        this.contentStart = contentStart;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the content end text.
     * </p>
     *
     * @return the current content end text
     */
    protected String getContentEnd() {
        return this.contentEnd;
    }

    /**
     * <p>
     * Sets the content end text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param contentEnd the new content end text
     */
    protected void setContentEnd(String contentEnd) {
        if (contentEnd == null) {
            contentEnd = StringUtils.EMPTY;
        }
        this.contentEnd = contentEnd;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the field name value separator text.
     * </p>
     *
     * @return the current field name value separator text
     */
    protected String getFieldNameValueSeparator() {
        return this.fieldNameValueSeparator;
    }

    /**
     * <p>
     * Sets the field name value separator text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param fieldNameValueSeparator the new field name value separator text
     */
    protected void setFieldNameValueSeparator(String fieldNameValueSeparator) {
        if (fieldNameValueSeparator == null) {
            fieldNameValueSeparator = StringUtils.EMPTY;
        }
        this.fieldNameValueSeparator = fieldNameValueSeparator;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the field separator text.
     * </p>
     *
     * @return the current field separator text
     */
    protected String getFieldSeparator() {
        return this.fieldSeparator;
    }

    /**
     * <p>
     * Sets the field separator text.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param fieldSeparator the new field separator text
     */
    protected void setFieldSeparator(String fieldSeparator) {
        if (fieldSeparator == null) {
            fieldSeparator = StringUtils.EMPTY;
        }
        this.fieldSeparator = fieldSeparator;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether the field separator should be added at the start of each
     * buffer.
     * </p>
     *
     * @return the fieldSeparatorAtStart flag
     * @since 2.0
     */
    protected boolean isFieldSeparatorAtStart() {
        return this.fieldSeparatorAtStart;
    }

    /**
     * <p>
     * Sets whether the field separator should be added at the start of each
     * buffer.
     * </p>
     *
     * @param fieldSeparatorAtStart the fieldSeparatorAtStart flag
     * @since 2.0
     */
    protected void setFieldSeparatorAtStart(final boolean fieldSeparatorAtStart) {
        this.fieldSeparatorAtStart = fieldSeparatorAtStart;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets whether the field separator should be added at the end of each
     * buffer.
     * </p>
     *
     * @return fieldSeparatorAtEnd flag
     * @since 2.0
     */
    protected boolean isFieldSeparatorAtEnd() {
        return this.fieldSeparatorAtEnd;
    }

    /**
     * <p>
     * Sets whether the field separator should be added at the end of each
     * buffer.
     * </p>
     *
     * @param fieldSeparatorAtEnd the fieldSeparatorAtEnd flag
     * @since 2.0
     */
    protected void setFieldSeparatorAtEnd(final boolean fieldSeparatorAtEnd) {
        this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the text to output when <code>null</code> found.
     * </p>
     *
     * @return the current text to output when null found
     */
    protected String getNullText() {
        return this.nullText;
    }

    /**
     * <p>
     * Sets the text to output when <code>null</code> found.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param nullText the new text to output when null found
     */
    protected void setNullText(String nullText) {
        if (nullText == null) {
            nullText = StringUtils.EMPTY;
        }
        this.nullText = nullText;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the start text to output when a <code>Collection</code>,
     * <code>Map</code> or array size is output.
     * </p>
     * <p>
     * This is output before the size value.
     * </p>
     *
     * @return the current start of size text
     */
    protected String getSizeStartText() {
        return this.sizeStartText;
    }

    /**
     * <p>
     * Sets the start text to output when a <code>Collection</code>,
     * <code>Map</code> or array size is output.
     * </p>
     * <p>
     * This is output before the size value.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param sizeStartText the new start of size text
     */
    protected void setSizeStartText(String sizeStartText) {
        if (sizeStartText == null) {
            sizeStartText = StringUtils.EMPTY;
        }
        this.sizeStartText = sizeStartText;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the end text to output when a <code>Collection</code>,
     * <code>Map</code> or array size is output.
     * </p>
     * <p>
     * This is output after the size value.
     * </p>
     *
     * @return the current end of size text
     */
    protected String getSizeEndText() {
        return this.sizeEndText;
    }

    /**
     * <p>
     * Sets the end text to output when a <code>Collection</code>,
     * <code>Map</code> or array size is output.
     * </p>
     * <p>
     * This is output after the size value.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param sizeEndText the new end of size text
     */
    protected void setSizeEndText(String sizeEndText) {
        if (sizeEndText == null) {
            sizeEndText = StringUtils.EMPTY;
        }
        this.sizeEndText = sizeEndText;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the start text to output when an <code>Object</code> is output in
     * summary mode.
     * </p>
     * <p>
     * This is output before the size value.
     * </p>
     *
     * @return the current start of summary text
     */
    protected String getSummaryObjectStartText() {
        return this.summaryObjectStartText;
    }

    /**
     * <p>
     * Sets the start text to output when an <code>Object</code> is output in
     * summary mode.
     * </p>
     * <p>
     * This is output before the size value.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param summaryObjectStartText the new start of summary text
     */
    protected void setSummaryObjectStartText(String summaryObjectStartText) {
        if (summaryObjectStartText == null) {
            summaryObjectStartText = StringUtils.EMPTY;
        }
        this.summaryObjectStartText = summaryObjectStartText;
    }

    //---------------------------------------------------------------------

    /**
     * <p>
     * Gets the end text to output when an <code>Object</code> is output in
     * summary mode.
     * </p>
     * <p>
     * This is output after the size value.
     * </p>
     *
     * @return the current end of summary text
     */
    protected String getSummaryObjectEndText() {
        return this.summaryObjectEndText;
    }

    /**
     * <p>
     * Sets the end text to output when an <code>Object</code> is output in
     * summary mode.
     * </p>
     * <p>
     * This is output after the size value.
     * </p>
     * <p>
     * <code>null</code> is accepted, but will be converted to an empty String.
     * </p>
     *
     * @param summaryObjectEndText the new end of summary text
     */
    protected void setSummaryObjectEndText(String summaryObjectEndText) {
        if (summaryObjectEndText == null) {
            summaryObjectEndText = StringUtils.EMPTY;
        }
        this.summaryObjectEndText = summaryObjectEndText;
    }

    // ----------------------------------------------------------------------------

    /**
     * <p>
     * <code>ToStringStyle</code> that outputs with JSON format.
     * </p>
     * <p>
     * This is an inner class rather than using
     * <code>StandardToStringStyle</code> to ensure its immutability.
     * </p>
     *
     * @since 3.4
     * @see <a href="http://json.org">json.org</a>
     */
    private static final class JsonToStringStyle extends ToStringStyle {

        private static final long serialVersionUID  = 1L;

        /**
         * The summary size text start <code>'&gt;'</code>.
         */
        private String            FIELD_NAME_PREFIX = "\"";

        /**
         * <p>
         * Constructor.
         * </p>
         * <p>
         * Use the static constant rather than instantiating.
         * </p>
         */
        JsonToStringStyle() {
            super();

            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);

            this.setContentStart("{");
            this.setContentEnd("}");

            this.setArrayStart("[");
            this.setArrayEnd("]");

            this.setFieldSeparator(",");
            this.setFieldNameValueSeparator(":");

            this.setNullText("null");

            this.setSummaryObjectStartText("\"<");
            this.setSummaryObjectEndText(">\"");

            this.setSizeStartText("\"<size=");
            this.setSizeEndText(">\"");
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, long[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, int[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, short[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, byte[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, char[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, double[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, float[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, boolean[] array, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, array, fullDetail);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }

            super.append(buffer, fieldName, value, fullDetail);
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, char value) {
            this.appendValueAsString(buffer, String.valueOf(value));
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {

            if (value == null) {
                this.appendNullText(buffer, fieldName);
                return;
            }

            if (value instanceof String || value instanceof Character) {
                this.appendValueAsString(buffer, value.toString());
                return;
            }

            if (value instanceof Number || value instanceof Boolean) {
                buffer.append(value);
                return;
            }

            final String valueAsString = value.toString();
            if (this.isJsonObject(valueAsString) || this.isJsonArray(valueAsString)) {
                buffer.append(value);
                return;
            }

            this.appendDetail(buffer, fieldName, valueAsString);
        }

        private boolean isJsonArray(String valueAsString) {
            return valueAsString.startsWith(this.getArrayStart()) && valueAsString.startsWith(this.getArrayEnd());
        }

        private boolean isJsonObject(String valueAsString) {
            return valueAsString.startsWith(this.getContentStart()) && valueAsString.endsWith(this.getContentEnd());
        }

        /**
         * Appends the given String in parenthesis to the given StringBuffer.
         * 
         * @param buffer the StringBuffer to append the value to.
         * @param value the value to append.
         */
        private void appendValueAsString(StringBuffer buffer, String value) {
            if (StringUtils.isBlank(value)
                    || value.startsWith(this.getArrayStart()) && value.endsWith(this.getArrayEnd())
                    || value.startsWith(this.getContentStart()) && value.endsWith(this.getContentEnd())) {
                buffer.append(value);
            } else {
                buffer.append("\"" + value + "\"");
            }
        }

        @Override
        protected void appendFieldStart(StringBuffer buffer, String fieldName) {

            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }

            super.appendFieldStart(buffer, this.FIELD_NAME_PREFIX + fieldName + this.FIELD_NAME_PREFIX);
        }

        /**
         * <p>
         * Ensure <code>Singleton</code> after serialization.
         * </p>
         *
         * @return the singleton
         */
        private Object readResolve() {
            return JToStringStyle.JSON_STYLE;
        }
    }
}
