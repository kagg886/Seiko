package top.dsbbs2.t544;

import java.util.Arrays;

public class State {
    public Long[]/*uint[]*/ state = new Long[16];
    public Long[]/*uint[]*/ org_state = new Long[16];
    public short/*ubyte*/ nr;
    public short/*ubyte*/ p;

    {
        Arrays.fill(state, 0L);
    }

    {
        Arrays.fill(org_state, 0L);
    }
}
