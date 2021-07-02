package org.yzh.protocol.t808;

import io.github.yezhihao.protostar.DataType;
import io.github.yezhihao.protostar.annotation.Field;
import io.github.yezhihao.protostar.annotation.Message;
import org.yzh.protocol.basics.JTMessage;
import org.yzh.protocol.commons.JT808;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yezhihao
 * @home https://gitee.com/yezhihao/jt808-server
 * 该消息2019版本已删除
 */
@Message(JT808.信息点播菜单设置)
public class T8303 extends JTMessage {

    /** @see org.yzh.protocol.commons.Action */
    @Field(index = 0, type = DataType.BYTE, desc = "设置类型")
    private int type;
    @Field(index = 1, type = DataType.BYTE, desc = "设置总数")
    private int total;
    @Field(index = 2, type = DataType.LIST, desc = "事件项列表")
    private List<Item> items;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTotal() {
        if (items != null)
            return items.size();
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        this.total = items.size();
    }

    public void addItem(int id, String content) {
        if (items == null)
            items = new ArrayList();
        items.add(new Item(id, content));
        total = items.size();
    }

    public static class Item {
        @Field(index = 0, type = DataType.BYTE, desc = "事件ID")
        private int id;
        @Field(index = 1, type = DataType.STRING, lengthSize = 2, desc = "信息名称")
        private String content;

        public Item() {
        }

        public Item(int id, String content) {
            this.id = id;
            this.content = content;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(40);
            sb.append("{id=").append(id);
            sb.append(",content=").append(content);
            sb.append('}');
            return sb.toString();
        }
    }
}