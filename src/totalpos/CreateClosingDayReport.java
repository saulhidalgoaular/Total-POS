    package totalpos;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.SubreportBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * @author Saul Hidalgo (Basado en DynamicReports!)
 */

public class CreateClosingDayReport {

    private String myDay = "";
    private String note = "";
    private Double total;

    public CreateClosingDayReport(String myDay, String obs, Double ltotal) {
        this.myDay = myDay;
        this.note = obs;
        this.total = ltotal;
        build();
    }

    private void build() {
        SubreportBuilder subreport = cmp.subreport(new SubreportExpression())
                                        .setDataSource(new SubreportDataSourceExpression());

        try {
            JasperReportBuilder jrb = report()
              .title(Templates.createTitleComponent("Reporte de Cierre"))
              .detail(
                subreport,
                cmp.verticalGap(20))
              .pageFooter(Templates.footerComponent)
              .setDataSource(createDataSource())
              .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT);
            JasperViewer jv = new JasperViewer(jrb.toJasperPrint(), false);
            jv.setTitle(Constants.appName);
            jv.setVisible(true);;
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    private JRDataSource createDataSource() {
        return new JREmptyDataSource(4);
    }

    private class SubreportExpression extends AbstractSimpleExpression<JasperReportBuilder> {
        private static final long serialVersionUID = 1L;

        public JasperReportBuilder evaluate(ReportParameters reportParameters) {
            int masterRowNumber = reportParameters.getReportRowNumber();
            JasperReportBuilder report = report();
            report
              .setTemplate(Templates.reportTemplate);

            if(masterRowNumber == 1){
                //try {
                    String[] toks = myDay.split("-");
                    report.title(cmp.text("Agencia "+ Shared.getConfig("storeName") + "\nCorrespondiente al " + toks[2] + "-"+ toks[1] + "-" + toks[0] + "\n\n" +
                            "Total ventas del día (B): "
                            //+ Constants.df.format(ConnectionDrivers.getTotalDeclared(myDay) * (Shared.getIva() + 100.0) / 100.0)
                            + total
                            + " Bs" + "\n\nFondos de Caja (A)").setStyle(Templates.bold12CenteredStyle));
                    TextColumnBuilder tcb = col.column("Monto", "2", type.bigDecimalType());
                    report.addColumn(col.column("Caja", "0", type.stringType()));
                    report.addColumn(col.column("Fecha", "1", type.stringType()));
                    report.addColumn(tcb);
                /*} catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                }*/
            }else if ( masterRowNumber == 2 ){
                report.title(cmp.text("Gastos (C)").setStyle(Templates.bold12CenteredStyle));
                TextColumnBuilder tcb = col.column("Monto", "2", type.bigDecimalType());
                report.addColumn(col.column("Tipo de Gasto", "0", type.stringType()));
                report.addColumn(col.column("Observaciones", "1", type.stringType()));
                report.addColumn(tcb);
                report.subtotalsAtSummary((AggregationSubtotalBuilder<BigDecimal>)sbt.sum(tcb).setLabel("Total Egresos"));
            } else if ( masterRowNumber == 3 ){
                report.title(cmp.text("Depositos (D)").setStyle(Templates.bold12CenteredStyle));
                TextColumnBuilder tcb = col.column("Monto", "3", type.bigDecimalType());
                report.addColumn(col.column("Tipo de Ingreso", "0", type.stringType()));
                report.addColumn(col.column("Nombre del Banco", "1", type.stringType()));
                report.addColumn(col.column("Lote", "2", type.stringType()));
                report.addColumn(tcb);
                report.subtotalsAtSummary((AggregationSubtotalBuilder<BigDecimal>)sbt.sum(tcb).setLabel("Total Ingresos"));
            } else if ( masterRowNumber == 4 ) {
                try {
                    Double receiptTotal = ConnectionDrivers.getSumTotalWithIva(myDay,"factura","Facturada", true , null) - ConnectionDrivers.getSumTotalWithIva(myDay,"nota_de_credito","Nota",false, null);
                    Double income = ConnectionDrivers.getTotalIncomming(myDay);
                    System.out.println(income);
                    Double amc = ConnectionDrivers.getTotalAMinusC(myDay);
                    /*System.out.println(ConnectionDrivers.getTotalDeclared(myDay) * (Shared.getIva() + 100.0) / 100.0);
                    System.out.println(ConnectionDrivers.getTotalPrinters(myDay));*/
                    report.title(cmp.text(
                            Shared.formatIt("Cuadre de Cajas (B-D):",Shared.df.format(Shared.round(total-income,2))) + "\n" +
                            Shared.formatIt("Cuadre de Cajas del Día (A+B-C-D): ",Shared.df.format(total-income+amc)) + "\n"

                            + Shared.formatIt("Diff entre Impresoras Fiscales y Sistema (B-E) ", Shared.df.format(total - ConnectionDrivers.getTotalPrinters(myDay)))+
                            "\n\nObservaciones: Cantidad de Notas de Créditos: " +
                            ConnectionDrivers.getQuantCN(myDay) + "      Monto: " +
                            Shared.df.format((ConnectionDrivers.getTotalCN(myDay) *(Shared.getIva()+100.0)/100.0))
                            + "\n" + note 
                            + "\nImpresoras Fiscales (E)").setStyle(Templates.bold12CenteredStyle));
                    TextColumnBuilder tcb = col.column("Monto", "2", type.bigDecimalType());
                    report.addColumn(col.column("Maquina Fiscal Nro", "0", type.stringType()));
                    report.addColumn(col.column("Numero Z", "1", type.stringType()));
                    report.addColumn(tcb);
                    report.subtotalsAtSummary((AggregationSubtotalBuilder<BigDecimal>) sbt.sum(tcb).setLabel("Total Ingresos"));
                } catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{

                report.title(cmp.text("Subreport" + masterRowNumber).setStyle(Templates.bold12CenteredStyle));
                for (int i = 1; i <= masterRowNumber; i++) {
                  report.addColumn(col.column("Column" + i, "column" + i, type.stringType()));
                }
            }

            return report;
        }
    }

    private class SubreportDataSourceExpression extends AbstractSimpleExpression<JRDataSource> {
        private static final long serialVersionUID = 1L;

        @Override
        public JRDataSource evaluate(ReportParameters reportParameters) {
            int masterRowNumber = reportParameters.getReportRowNumber();

            
            if ( masterRowNumber == 1 ){
                try {
                    return ConnectionDrivers.getInitialFounds(myDay);
                } catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                    String[] columns = {};
                    return new DataSource(columns);
                }
            }else if ( masterRowNumber == 2 ){
                try {
                    return ConnectionDrivers.getExpensesReport(myDay);
                } catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                    String[] columns = {};
                    return new DataSource(columns);
                }
            } if ( masterRowNumber == 3 ) {
                try {
                    return ConnectionDrivers.getIncommingReport(myDay);
                } catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                    String[] columns = {};
                    return new DataSource(columns);
                }
            } if ( masterRowNumber == 4 ) {
                try {
                    return ConnectionDrivers.getFiscalInfo(myDay);
                } catch (SQLException ex) {
                    Logger.getLogger(CreateClosingDayReport.class.getName()).log(Level.SEVERE, null, ex);
                    String[] columns = {};
                    return new DataSource(columns);
                }
            }else{

                String[] columns = new String[masterRowNumber];
                for (int i = 1; i <= masterRowNumber; i++) {
                    columns[i - 1] = "column" + i;
                }

                DataSource dataSource = new DataSource(columns);

                for (int i = 1; i <= masterRowNumber; i++) {
                    Object[] values = new Object[masterRowNumber];
                    for (int j = 1; j <= masterRowNumber; j++) {
                        values[j - 1] = "row" + i + "_column" + j;
                    }
                    dataSource.add(values);
                }
                return dataSource;
            }
        }
    }
}
