package com.dwly.myfile.filebrowser.view;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public interface IView {
    void showPrompt(String prompt);
    void showLoadView();
    void hideLoadView();
    void onOpenFileList(String path, List<File> fileList);
    void onRefreshFileList();
}
