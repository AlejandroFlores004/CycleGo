package com.ues.edu.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DatabaseBackupService {

    void generarBackup(OutputStream outputStream);

    void restaurarBackup(InputStream backupStream);
}
