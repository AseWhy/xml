package io.github.asewhy.xml;

import io.github.asewhy.processors.support.CommonBuilderWriter;
import io.github.asewhy.processors.support.StreamWrapperWriter;
import io.github.asewhy.processors.support.interfaces.iWriter;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class XmlGenerator {
    private static final DateFormat PARSABLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final Double XML_VERSION = 1.0d;
    private static final String XML_ENCODING = "UTF-8";

    private static final String HOOK = "\"";
    private static final String TAG_PARAM_EQUALS = "=";
    private static final String SPACE = " ";
    private static final String CLOSE_TAG_START = "</";
    private static final String OPEN_TAG_START = "<";
    private static final String TAG_END = ">";
    private static final String NAMESPACE_SEPO = ":";

    private String encoding;
    private Double version;
    private long state;
    private DateFormat currentFormat;
    private String defaultNamespace;
    private final iWriter writer;
    private final LinkedList<String> tagStack;

    private XmlGenerator(@NotNull iWriter writer, @NotNull DateFormat format) {
        this.writer = writer;
        this.state = 0x0;
        this.currentFormat = format;
        this.defaultNamespace = "xmlns";
        this.version = XML_VERSION;
        this.encoding = XML_ENCODING;
        this.tagStack = new LinkedList<>();
    }

    /**
     * Установить текущий формат даты
     *
     * @param currentFormat текущий формат даты
     */
    public XmlGenerator setCurrentFormat(@NotNull final DateFormat currentFormat) {
        this.currentFormat = currentFormat; return this;
    }

    /**
     * Устанавливает стандартное пространство имен.
     *
     * @param defaultNamespace пространство имен по умолчанию
     */
    public XmlGenerator setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace; return this;
    }

    /**
     * Устанавливает версию xml
     *
     * @param version версия
     */
    public XmlGenerator setVersion(Double version) {
        this.version = version; return this;
    }

    /**
     * Устанавливает кодировку xml
     *
     * @param encoding кодировка
     */
    public XmlGenerator setEncoding(String encoding) {
        this.encoding = encoding; return this;
    }

    /**
     * Выводит переданную первым параметром строку
     *
     * @param write строка для вывода
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator write(Object write) {
        if(write == null) {
            writer.write("null");
        } else {
            if(write instanceof Date) {
                this.writer.write(currentFormat.format(write));
            } else if(write instanceof String) {
                this.writer.write((String) write);
            } else if(write instanceof Number) {
                if(write instanceof Double) {
                    this.writer.write(String.format("%.2f", write));
                } else if(write instanceof Float) {
                    this.writer.write(String.format("%.2f", write));
                }
            } else {
                this.writer.write(write.toString());
            }
        }

        return this;
    }

    /**
     * Выводит заголовки xml
     *
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeXMLStart() {
        writeOpenTag("?xml");
        writeAttribute("version", String.format(Locale.US, "%.1f", version));
        writeAttribute("encoding", encoding);
        writeCloseTag("?");
        tagStack.clear();
        return this;
    }

    /**
     * Выводит аттрибут, или значение аттрибута, в зависимости от итерации
     *
     * @param name название или значение аттрибута в зависимости от итерации
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeAttribute(String name) {
        writer.write(SPACE);
        writer.write(name);
        return this;
    }

    /**
     * Выводит аттрибут и значение аттрибута
     *
     * @param name название атрибута
     * @param value значение атрибута
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeAttribute(String name, String value) {
        writer.write(SPACE);
        writer.write(name);
        writer.write(TAG_PARAM_EQUALS);
        writer.write(HOOK);
        writer.write(safeXml(value));
        writer.write(HOOK);
        return this;
    }

    /**
     * Выводит пространство имен, аттрибут и значение аттрибута
     *
     * @param namespace пространство имен атрибута
     * @param name название атрибута
     * @param value значение атрибута
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeAttribute(String namespace, String name, String value) {
        writer.write(SPACE);
        writer.write(namespace);
        writer.write(NAMESPACE_SEPO);
        writer.write(name);
        writer.write(TAG_PARAM_EQUALS);
        writer.write(HOOK);
        writer.write(safeXml(value));
        writer.write(HOOK);
        return this;
    }

    /**
     * Выводит пространство имен, аттрибут, или значение аттрибута, в зависимости от итерации
     *
     * @param name пространство имен, название или значение в зависимости аттрибута от итерации
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeAttributeWithDefNs(String name) {
        return this;
    }

    /**
     * Выводит стандартное пространство имен, аттрибут и значение аттрибута
     *
     * @param name название атрибута
     * @param value значение атрибута
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeAttributeWithDefNs(String name, String value) {
        writer.write(SPACE);
        writer.write(defaultNamespace);
        writer.write(NAMESPACE_SEPO);
        writer.write(name);
        writer.write(TAG_PARAM_EQUALS);
        writer.write(HOOK);
        writer.write(safeXml(value));
        writer.write(HOOK);
        return this;
    }

    /**
     * Выводит закрывающий тег
     *
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeCloseTag() {
        writer.write(TAG_END);
        return this;
    }

    /**
     * Выводит закрывающий тег и суффикс
     *
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeCloseTag(String suffix) {
        writer.write(suffix);
        writer.write(TAG_END);
        return this;
    }

    /**
     * Выводит открывающийся тег, имя
     *
     * @param name название тега, без пространства имен
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeOpenTag(String name) {
        writer.write(OPEN_TAG_START);
        writer.write(name);
        addToStack(null, name);
        return this;
    }

    /**
     * Выводит открывающийся тег, значение пространства имен и имя &lt;new_namespace:new_name&gt;...
     *
     * @param namespace пространство имен
     * @param name имя поля
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeOpenTag(String namespace, String name) {
        writer.write(OPEN_TAG_START);

        if(namespace != null && !namespace.isEmpty()) {
            writer.write(namespace);
            writer.write(NAMESPACE_SEPO);
        }

        writer.write(name);

        addToStack(namespace, name);

        return this;
    }

    /**
     * Выводит открывающийся тег, и закрывает его без атрибутов
     *
     * @param name название тега, без пространства имен
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeNodeStart(String name) {
        writeOpenTag(name);
        writeCloseTag();
        return this;
    }

    /**
     * Выводит открывающийся тег, и закрывает его без атрибутов
     *
     * @param namespace пространство имен
     * @param name имя поля
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeNodeStart(String namespace, String name) {
        writeOpenTag(namespace, name);
        writeCloseTag();
        return this;
    }

    /**
     * Выводит открывающийся тег, значение пространства имен по умолчанию и имя &lt;new_namespace:new_name&gt;...
     *
     * @param name имя поля
     * @return генератор {@link XmlGenerator} {
     */
    public XmlGenerator writeOpenTagWithDefaultNs(String name) {
        return writeOpenTag(defaultNamespace, name);
    }

    /**
     * Записывает узел по пространству имен, имени и значению
     *
     * @param namespace пространство имен
     * @param name имя
     * @param value значние
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeNode(String namespace, String name, String value) {
        writeOpenTag(namespace, name);
        writeCloseTag();
        write(value);
        writeCloseNode(namespace, name);
        return this;
    }

    /**
     * Записывает узел по пространству имен и имени
     *
     * @param namespace пространство имен
     * @param name имя
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeNode(String namespace, String name) {
        writeOpenTag(namespace, name);
        writeCloseTag();
        writeCloseNode(namespace, name);
        return this;
    }

    /**
     * Записывает узел по имени
     *
     * @param name имя
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeNode(String name) {
        writeOpenTag(name);
        writeCloseTag();
        writeCloseNode(name);
        return this;
    }

    /**
     * Закрывает текущий узел ... &lt;/last_namespace:last_name&gt;, при этом последнее пространство имен и последнее имя будет подставлено автоматически
     *
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeCloseNode() {
        if(tagStack.size() > 0) {
            writer.write(CLOSE_TAG_START);
            writer.write(tagStack.removeLast());
            writer.write(TAG_END);
        } else {
            throw new RuntimeException("No has more tags...");
        }

        return this;
    }

    /**
     * Закрывает текущий узел ... &lt;/last_namespace:last_name&gt;, с учетом имени переданного как параметр
     *
     * @param name название узла
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeCloseNode(String name) {
        writer.write(CLOSE_TAG_START);
        writer.write(name);
        writer.write(TAG_END);
        tagStack.remove(name);
        return this;
    }

    /**
     * Закрывает текущий узел ... &lt;/last_namespace:last_name&gt;, с учетом имени и пространства имен переданных как параметров
     *
     * @param namespace пространство имен
     * @param name название узла
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeCloseNode(String namespace, String name) {
        var tag = (namespace != null ? namespace + NAMESPACE_SEPO : "") + name;
        writer.write(CLOSE_TAG_START);
        writer.write(tag);
        writer.write(TAG_END);
        tagStack.remove(tag);
        return this;
    }

    /**
     * Закрывает текущий узел ... &lt;/last_namespace:last_name&gt;, с учетом пространства имен по умолчанию
     *
     * @param name название узла
     * @return генератор {@link XmlGenerator}
     */
    public XmlGenerator writeCloseNodeWithDefaultNs(String name) {
        return writeCloseNode(defaultNamespace, name);
    }

    /**
     * Обезопасить строку json от кавычек...
     *
     * @param some строка
     * @return безопасная строка
     */
    private static String safeXml(String some) {
        return some.replaceAll("\"", "\\\\\"");
    }

    /**
     * Добавляет открытый тег в стэк
     *
     * @param namespace пространство имен
     * @param name название тега
     */
    private void addToStack(String namespace, String name ){
        if(namespace != null && name != null) {
            tagStack.add(namespace + ":" + name);
        } else if(name != null) {
            tagStack.add(name);
        } else if(namespace != null) {
            tagStack.add(namespace + ":");
        }
    }

    /**
     * Включить флаг состояния
     *
     * @param state флаг состояния
     */
    private void addState(long state) {
        this.state |= state;
    }

    /**
     * Выключить флаг состояния
     *
     * @param state флаг состояния
     */
    private void subtractState(long state) {
        this.state ^= state;
    }

    /**
     * Проверяет, имеется ли текущее состояние у объекта
     *
     * @param state флаг состояния
     */
    private boolean haveState(long state) {
        return (this.state & state) != 0;
    }

    /**
     * Преобразовывает генератор к строковому значению
     *
     * @return строковое значение, зависит от реализации {@link iWriter}
     */
    @Override
    public String toString() {
        return this.writer.toString();
    }

    /**
     * Получить версию xml
     *
     * @return версия xml
     */
    public Double getVersion() {
        return version;
    }

    /**
     * Получить кодировку xml
     *
     * @return кодировка xml
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Получить формат даты, для преобразования
     *
     * @return формат даты для рпеобразования
     */
    public DateFormat getCurrentFormat() {
        return currentFormat;
    }

    /**
     * Получить стандартное пространство имен
     *
     * @return стандартное пространство имен
     */
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    //
    // Методы-фабрики
    //

    public static XmlGenerator from(@NotNull StringBuilder builder) {
        return new XmlGenerator(new CommonBuilderWriter(builder), PARSABLE_DATE_FORMAT);
    }

    public static XmlGenerator from(@NotNull OutputStream stream) {
        return new XmlGenerator(new StreamWrapperWriter(stream), PARSABLE_DATE_FORMAT);
    }

    public static XmlGenerator from(@NotNull StringBuilder builder, @NotNull DateFormat format) {
        return new XmlGenerator(new CommonBuilderWriter(builder), format);
    }

    public static XmlGenerator from(@NotNull OutputStream stream, @NotNull DateFormat format) {
        return new XmlGenerator(new StreamWrapperWriter(stream), format);
    }

    public static XmlGenerator from(@NotNull DateFormat format) {
        return new XmlGenerator(new CommonBuilderWriter(new StringBuilder()), format);
    }

    public static XmlGenerator common() {
        return new XmlGenerator(new CommonBuilderWriter(new StringBuilder()), PARSABLE_DATE_FORMAT);
    }
}
