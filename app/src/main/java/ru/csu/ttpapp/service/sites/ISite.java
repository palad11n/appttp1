package ru.csu.ttpapp.service.sites;

public interface ISite {
    void findUpDate(SiteUpdate.ICompleteCallback iCompleteCallback);
    void getTitleSite(SiteUpdate.ICompleteCallbackTitle iCompleteCallback);
}
