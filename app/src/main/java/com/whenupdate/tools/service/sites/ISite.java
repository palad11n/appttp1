package com.whenupdate.tools.service.sites;

public interface ISite {
    void findUpDate(SiteUpdate.ICompleteCallback iCompleteCallback);
    void findDate(SiteUpdate.ICompleteCallback iCompleteCallback);
    void getTitleSite(SiteUpdate.ICompleteCallbackTitle iCompleteCallback);
}
