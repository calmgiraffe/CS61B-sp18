package byog.Core;

import java.util.List;

public interface Visitable {
    void accept(Visitor visitor);
    List<Visitable> getVisitables();
}
