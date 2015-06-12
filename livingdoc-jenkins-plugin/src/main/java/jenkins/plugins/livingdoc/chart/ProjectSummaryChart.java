/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package jenkins.plugins.livingdoc.chart;

import hudson.util.Graph;
import info.novatec.testit.Statistics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Calendar;
import java.util.List;

import jenkins.plugins.livingdoc.SummaryBuildReportBean;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;


public class ProjectSummaryChart extends Graph {

    private static final Color GREEN_COLOR = new Color(Integer.parseInt("33cc00", 16));

    public static final String SUCCESS_SERIES_NAME = "Success";
    public static final String FAILURES_SERIES_NAME = "Failures";

    private static final int SUCCESS_SERIES = 0;
    private static final int FAILURES_SERIES = 1;

    private static final int DEFAULT_CHART_WIDTH = 750;
    private static final int DEFAULT_CHART_HEIGHT = 450;

    private List<SummaryBuildReportBean> summaries;

    private int lowerBoundCount = Integer.MAX_VALUE;
    private int upperBoundCount = 0;

    public ProjectSummaryChart (Calendar timestamp, List<SummaryBuildReportBean> summaries) {
        super(timestamp, DEFAULT_CHART_WIDTH, DEFAULT_CHART_HEIGHT);

        this.summaries = summaries;
    }

    @Override
    protected JFreeChart createGraph () {
        return createChart(aggregateDataset());
    }

    private DefaultCategoryDataset aggregateDataset () {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (SummaryBuildReportBean summary : summaries) {
            Statistics stats = summary.getBuildSummary().getStatistics();
            String label = String.format("#%d", summary.getBuildId());
            int failureCount = stats.exceptionCount() + stats.wrongCount();

            dataset.addValue(stats.rightCount(), SUCCESS_SERIES_NAME, label);
            dataset.addValue(failureCount, FAILURES_SERIES_NAME, label);

            adjustUpperBound(stats);
            adjustLowerBound(stats, failureCount);
        }

        return dataset;
    }

    private JFreeChart createChart (DefaultCategoryDataset dataset) {

        JFreeChart chart =
            ChartFactory.createLineChart("", "Build ID", "# Tests", dataset, PlotOrientation.VERTICAL, true, true, false);
        customizeChart(chart);

        return chart;
    }

    private void customizeChart (JFreeChart chart) {

        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = ( CategoryPlot ) chart.getPlot();

        adjustBound(plot.getRangeAxis());

        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(SUCCESS_SERIES, GREEN_COLOR);
        renderer.setSeriesPaint(FAILURES_SERIES, Color.red);

        LineAndShapeRenderer lineAndShapeRenderer = ( LineAndShapeRenderer ) renderer;
        lineAndShapeRenderer.setBaseItemLabelGenerator(new NumberLabelGenerator());
        lineAndShapeRenderer.setBaseItemLabelsVisible(true);
        lineAndShapeRenderer.setBaseShapesVisible(true);
        lineAndShapeRenderer.setDrawOutlines(true);
        lineAndShapeRenderer.setUseFillPaint(true);

        renderer.setBaseStroke(new BasicStroke(2.0f));
    }

    private void adjustBound (ValueAxis valueAxis) {
        // Since we are showing the ItemLabel on top, add a gap to the
        // upper-bound value to make sure the
        // ItemLabel is fully visible (did try with ItemLabelPosition without
        // success!)
        // @todo : need to find a better solution
        int gap = ( int ) ( 20 * upperBoundCount / ( double ) DEFAULT_CHART_HEIGHT );

        valueAxis.setUpperBound(upperBoundCount + ( gap < 7 ? 7 : gap ));
        valueAxis.setLowerBound(lowerBoundCount);
    }

    private void adjustUpperBound (Statistics stats) {

        if (upperBoundCount < stats.totalCount()) {
            upperBoundCount = stats.totalCount();
        }
    }

    private void adjustLowerBound (Statistics stats, int failureCount) {

        int value = Math.min(stats.rightCount(), failureCount);

        if (lowerBoundCount > value) {
            lowerBoundCount = value;
        }
    }
}
