package es.udc.paproject.backend.test.model.common;

import es.udc.paproject.backend.model.services.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BlockTest {

    @Test
    @DisplayName("Equals: Reflexividade")
    void testEqualsReflexive() {
        Block<String> block = new Block<>(List.of("A"), false);
        assertEquals(block, block);
    }

    @Test
    @DisplayName("Equals: Simetría con obxectos con iguais datos")
    void testEqualsSymmetricSameData() {
        Block<String> block1 = new Block<>(List.of("A"), false);
        Block<String> block2 = new Block<>(List.of("A"), false);
        assertEquals(block1, block2);
    }

    @Test
    @DisplayName("Equals: Obxectos con diferente flag existMoreItems non son iguais")
    void testEqualsDifferentExistMoreItems() {
        Block<String> block1 = new Block<>(List.of("A"), true);
        Block<String> block2 = new Block<>(List.of("A"), false);
        assertNotEquals(block1, block2);
    }

    @Test
    @DisplayName("HashCode: Obxectos iguais teñen o mesmo hashCode")
    void testHashCodeConsistency() {
        Block<String> block1 = new Block<>(List.of("A"), false);
        Block<String> block2 = new Block<>(List.of("A"), false);
        assertEquals(block1.hashCode(), block2.hashCode());
    }
}