package netty.syncresponse.future;

import netty.syncresponse.entity.Data;

public class AckFuture<T extends Data> {

    private volatile T data;

    /**
     * 消息流水号
     */
    private volatile int seqId;

    public synchronized T getResult(long timeout) throws InterruptedException {
        wait(timeout);
        return data;
    }

    public synchronized void setCompleted(T data) {
        if (data.getSeqId() == seqId) {
            this.data = data;
            notify();
        }
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }

}
