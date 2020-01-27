package radin.core.output.midanalysis.constantexpressions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantExpressionDeterminerTest {
    
    @Test
    void performOp() {
        Number d = 5.0;
        Number i = 4;
    
        Number add = ConstantExpressionDeterminer.performOp(d, i, "+");
        Number sub = ConstantExpressionDeterminer.performOp(d, i, "-");
        
        assertNotNull(add);
        assertNotNull(sub);
        assertEquals(d.doubleValue() + i.intValue(), add);
        assertEquals(d.doubleValue() - i.intValue(), sub);
    }
}