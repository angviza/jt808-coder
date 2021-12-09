package org.yzh.protocol.commons.transform.parameter;

import io.github.yezhihao.protostar.annotation.Field;
import io.netty.buffer.ByteBuf;

/**
 * 图像分析报警参数设置
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public class ParamImageIdentifyAlarm {

    public static final int id = 0x007B;

    public static int id() {
        return id;
    }

    @Field(desc = "车辆核载人数,客运车辆核定载客人数,视频分析结果超过时产生报警")
    private byte overloadThreshold;
    @Field(desc = "疲劳程度阈值,视频分析疲劳驾驶报警阈值,超过时产生报警")
    private byte fatigueThreshold;

    public ParamImageIdentifyAlarm() {
    }

    public byte getOverloadThreshold() {
        return overloadThreshold;
    }

    public void setOverloadThreshold(byte overloadThreshold) {
        this.overloadThreshold = overloadThreshold;
    }

    public byte getFatigueThreshold() {
        return fatigueThreshold;
    }

    public void setFatigueThreshold(byte fatigueThreshold) {
        this.fatigueThreshold = fatigueThreshold;
    }

    public static class S implements io.github.yezhihao.protostar.Schema<ParamImageIdentifyAlarm> {

        public static final S INSTANCE = new S();

        private S() {
        }

        @Override
        public ParamImageIdentifyAlarm readFrom(ByteBuf input) {
            ParamImageIdentifyAlarm message = new ParamImageIdentifyAlarm();
            message.overloadThreshold = input.readByte();
            message.fatigueThreshold = input.readByte();
            return message;
        }

        @Override
        public void writeTo(ByteBuf output, ParamImageIdentifyAlarm message) {
            output.writeByte(message.overloadThreshold);
            output.writeByte(message.fatigueThreshold);
        }
    }
}