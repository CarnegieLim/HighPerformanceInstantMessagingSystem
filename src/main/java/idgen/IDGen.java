package idgen;

import idgen.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
