package netty.syncresponse.entity;

/**
 * 数据包实体类
 */
public class Data {

    private int seqId;

    private String msg;

    public Data(int seqId, String msg) {
        this.seqId = seqId;
        this.msg = msg;
    }

    public int getSeqId() {
        return seqId;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "Data{" +
                "seqId=" + seqId +
                ", msg='" + msg + '\'' +
                '}';
    }

}
