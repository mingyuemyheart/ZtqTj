package com.pcs.ztqtj.control.loading;

import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackBannerUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackHolidayInfoUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.PackShareAboutUp;
import com.pcs.ztqtj.control.command.AbstractCommand;
import com.pcs.ztqtj.control.tool.AutoDownloadWeather;

/**
 * 闪屏页面，请求一些首页数据
 */
public class CommandLoadingMainData extends AbstractCommand {
    @Override
    public void execute() {
        super.execute();
        setStatus(Status.SUCC);
        // 广告26
        PackBannerUp packBannerUp26 = new PackBannerUp();
        packBannerUp26.position_id = "26";
        PcsDataDownload.addDownload(packBannerUp26);
        // 广告26
        PackBannerUp packBannerUp27 = new PackBannerUp();
        packBannerUp27.position_id = "27";
        PcsDataDownload.addDownload(packBannerUp27);


        // 广告13
        PackBannerUp packBannerUp13 = new PackBannerUp();
        packBannerUp13.position_id = "13";
        PcsDataDownload.addDownload(packBannerUp13);
        // 广告14
        PackBannerUp packBannerUp14 = new PackBannerUp();
        packBannerUp14.position_id = "14";
        PcsDataDownload.addDownload(packBannerUp14);
        // 广告15
        PackBannerUp packBannerUp12 = new PackBannerUp();
        packBannerUp12.position_id = "12";
        PcsDataDownload.addDownload(packBannerUp12);

        // 广告11
        PackBannerUp packBannerUp11 = new PackBannerUp();
        packBannerUp11.position_id = "11";
        PcsDataDownload.addDownload(packBannerUp11);


        // 广告19
        PackBannerUp packBannerUp19 = new PackBannerUp();
        packBannerUp19.position_id = "19";
        PcsDataDownload.addDownload(packBannerUp19);

        // 广告22
        PackBannerUp packBannerUp22 = new PackBannerUp();
        packBannerUp22.position_id = "22";
        PcsDataDownload.addDownload(packBannerUp22);

        // 广告32
        PackBannerUp packBannerUp32 = new PackBannerUp();
        packBannerUp32.position_id = "32";
        PcsDataDownload.addDownload(packBannerUp32);

        // 广告32
        PackBannerUp packBannerUp35 = new PackBannerUp();
        packBannerUp35.position_id = "35";
        PcsDataDownload.addDownload(packBannerUp35);

        //首页数据
        AutoDownloadWeather.getInstance().setMainDataPause(false);
        AutoDownloadWeather.getInstance().beginMainData();

        // share 公共
        PackShareAboutUp share = new PackShareAboutUp();
        share.keyword="ABOUT_JC_DOWN";
        PcsDataDownload.addDownload(share);

        //获取节假日，
        PackHolidayInfoUp holiday = new PackHolidayInfoUp();
        PcsDataDownload.addDownload(holiday);
    }
}
