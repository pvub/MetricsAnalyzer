package com.tt.metrics;

/**
 *
 * @author Udai
 */
public class RowContainer
{
    public int rowindex        = 0;
    public int[] indices       = null;
    public double[] values     = null;

    public RowContainer(int index, int size)
    {
        rowindex    = index;
        indices     = new int[size];
        values      = new double[size];
    }
}
