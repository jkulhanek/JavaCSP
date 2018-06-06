package student;

import java.util.List;

public interface Variable<TValue>{
    String getName();
    boolean isAssigned();
    TValue getValue();
    List<TValue> getDomain();

	void setValue(TValue value);
}