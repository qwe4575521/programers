package _2018_01_15;

import myprogramers.Compare.CompareBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CompareMain {
    public static void main(String[] args) {
        ApplicationContext context=new FileSystemXmlApplicationContext("classpath:CheckDateSource.xml");

        String[] tables = {"SG_CON_COMPROOM_B", "SG_CON_COMPCABINET_B", "SG_CON_MASTERAS_B", "SG_CON_SLAVEAS_B", "SG_CON_DATANET_B", "SG_AUT_DATANETNODE_B", "SG_AUT_BLADESVRCON_B", "SG_AUT_BLADESVR_B", "SG_AUT_CRAC_B", "SG_AUT_CSMONITOR_B", "SG_AUT_FIREWALL_B", "SG_AUT_HORISOLATOR_B", "SG_AUT_IDD_B", "SG_AUT_INDUSSWITCH_B", "SG_AUT_KVM_B", "SG_AUT_OEC_B", "SG_AUT_PRINTER_B", "SG_AUT_ROUTER_B", "SG_AUT_SCREEN_B", "SG_AUT_SERVER_B", "SG_AUT_SPSERVER_B", "SG_AUT_STORAGE_B", "SG_AUT_SWITCH_B", "SG_AUT_TIMESYNCDEV_B", "SG_AUT_UPS_B", "SG_AUT_VERTENCRYPTOR_B", "SG_AUT_VIDEOWALL_B", "SG_AUT_WORKSTATION_B", "SG_SSD_BCU_B", "SG_SSD_ERTU_B", "SG_SSD_IED_B", "SG_SSD_MU_B", "SG_SSD_NETANALYZER_B", "SG_SSD_PMU_B", "SG_SSD_RTUGATEWAY_B", "SG_SSD_RTU_B", "SG_AUT_MSNODE_B", "SG_AUT_SSNODE_B", "SG_AUT_NETCONSTITUTE_B", "SG_AUT_DB_B", "SG_AUT_OS_B", "SG_AUT_MIDWARE_B", "SG_AUT_SOFTWAREPACK_B", "SG_AUT_SOFTWARE_B", "SG_AUT_PROGRAM_B", "SG_AUT_PORT_B", "SG_AUT_LINK_B"};
        for (int length = tables.length; length > 0; length--) {
            String tablename=tables[length].toUpperCase().trim().toString();








        }


    }
}
