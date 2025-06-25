package com.reljicd.util;

import com.reljicd.model.Product;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class PagerTest {

    @Test
    public void pagerCalculatesValues() {
        Product p1 = new Product();
        Product p2 = new Product();
        Page<Product> page = new PageImpl<>(Arrays.asList(p1, p2), new PageRequest(1, 2), 5);
        Pager pager = new Pager(page);

        assertEquals(2, pager.getPageIndex());
        assertEquals(2, pager.getPageSize());
        assertTrue(pager.hasNext());
        assertTrue(pager.hasPrevious());
        assertEquals(3, pager.getTotalPages());
        assertEquals(5, pager.getTotalElements());
        assertFalse(pager.indexOutOfBounds());
    }

    @Test
    public void pagerOutOfBoundsWhenPageIndexTooHigh() {
        Page<Product> page = new PageImpl<>(Collections.emptyList(), new PageRequest(10, 2), 5);
        Pager pager = new Pager(page);
        assertTrue(pager.indexOutOfBounds());
    }
}
