package org.yzh.protocol.codec;

import io.github.yezhihao.protostar.MultiVersionSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yzh.protocol.basics.JTMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 分包消息管理
 * @author yezhihao
 * https://gitee.com/yezhihao/jt808-server
 */
public class MultiPacketDecoder extends JTMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(MultiPacketDecoder.class.getSimpleName());

    private static final ConcurrentHashMap<String, MultiPacket> multiPacketsMap = new ConcurrentHashMap<>();

    private final MultiPacketListener multiPacketListener;

    public MultiPacketDecoder(String... basePackages) {
        this(new MultiVersionSchemaManager(basePackages));
    }

    public MultiPacketDecoder(MultiVersionSchemaManager schemaManager) {
        this(schemaManager, new MultiPacketListener(20));
    }

    public MultiPacketDecoder(MultiVersionSchemaManager schemaManager, MultiPacketListener multiPacketListener) {
        super(schemaManager);
        this.multiPacketListener = multiPacketListener;
        startListener();
    }

    @Override
    protected byte[][] addAndGet(JTMessage message, byte[] packetData) {
        String clientId = message.getClientId();
        int messageId = message.getMessageId();
        int packageTotal = message.getPackageTotal();
        int packetNo = message.getPackageNo();

        String key = new StringBuilder(21).append(clientId).append("/").append(messageId).append("/").append(packageTotal).toString();

        MultiPacket multiPacket = multiPacketsMap.get(key);
        if (multiPacket == null)
            multiPacketsMap.put(key, multiPacket = new MultiPacket(message));
        if (packetNo == 1)
            multiPacket.setSerialNo(message.getSerialNo());


        byte[][] packages = multiPacket.addAndGet(packetNo, packetData);
        log.info("<<<<<<<<<分包信息{}", multiPacket);
        if (packages == null)
            return null;
        multiPacketsMap.remove(key);
        return packages;
    }

    private void startListener() {
        Thread thread = new Thread(() -> {
            long timeout = multiPacketListener.timeout;
            for (; ; ) {
                long nextDelay = timeout;
                long now = System.currentTimeMillis();

                for (Map.Entry<String, MultiPacket> entry : multiPacketsMap.entrySet()) {
                    MultiPacket packet = entry.getValue();

                    long time = timeout - (now - packet.getLastAccessedTime());
                    if (time <= 0) {
                        if (!multiPacketListener.receiveTimeout(packet)) {
                            log.warn("<<<<<<<<<分包接收超时{}", packet);
                            multiPacketsMap.remove(entry.getKey());
                        }
                    } else {
                        nextDelay = Math.min(time, nextDelay);
                    }
                }
                try {
                    Thread.sleep(nextDelay);
                } catch (InterruptedException e) {
                    log.error("MultiPacketListener", e);
                }
            }
        });
        thread.setName("MultiPacketListener");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();
    }
}