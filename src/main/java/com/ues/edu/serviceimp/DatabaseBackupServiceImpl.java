package com.ues.edu.serviceimp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ues.edu.service.DatabaseBackupService;

@Service
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${db.backup.pg_dump_path}")
    private String pgDumpPath;

    @Value("${db.backup.pg_restore_path}")
    private String pgRestorePath;

    private static class DbInfo {
        String host;
        int port;
        String dbName;
    }

    private DbInfo parsePostgresJdbcUrl(String jdbcUrl) {
        // ejemplo: jdbc:postgresql://localhost:5432/cyclego_db
        try {
            String withoutPrefix = jdbcUrl.substring("jdbc:".length()); // postgresql://...
            URI uri = new URI(withoutPrefix);
            DbInfo info = new DbInfo();
            info.host = uri.getHost();
            info.port = (uri.getPort() == -1) ? 5432 : uri.getPort();
            info.dbName = uri.getPath().replaceFirst("/", "");
            return info;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo parsear la URL de la BD: " + jdbcUrl, e);
        }
    }

    @Override
    public void generarBackup(OutputStream outputStream) {
        DbInfo info = parsePostgresJdbcUrl(jdbcUrl);

        List<String> command = new ArrayList<>();
        command.add(pgDumpPath);
        command.add("-h");
        command.add(info.host);
        command.add("-p");
        command.add(String.valueOf(info.port));
        command.add("-U");
        command.add(dbUser);
        command.add("-F");
        command.add("c"); // formato "custom"
        command.add("-d");
        command.add(info.dbName);

        ProcessBuilder pb = new ProcessBuilder(command);
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", dbPassword); // para no pedirla interactivo
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            try (InputStream processIn = process.getInputStream()) {
                processIn.transferTo(outputStream);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("pg_dump terminó con código: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error generando backup con pg_dump", e);
        }
    }

    @Override
    public void restaurarBackup(InputStream backupStream) {
        DbInfo info = parsePostgresJdbcUrl(jdbcUrl);

        List<String> command = new ArrayList<>();
        command.add(pgRestorePath);
        command.add("-h");
        command.add(info.host);
        command.add("-p");
        command.add(String.valueOf(info.port));
        command.add("-U");
        command.add(dbUser);
        command.add("-d");
        command.add(info.dbName);
        command.add("-c"); // limpia objetos antes de restaurar

        ProcessBuilder pb = new ProcessBuilder(command);
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            // Enviamos el archivo al stdin de pg_restore
            try (OutputStream processOut = process.getOutputStream()) {
                backupStream.transferTo(processOut);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("pg_restore terminó con código: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error restaurando backup con pg_restore", e);
        }
    }
}
