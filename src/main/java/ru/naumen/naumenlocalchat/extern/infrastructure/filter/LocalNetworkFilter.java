package ru.naumen.naumenlocalchat.extern.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Фильтр запросов локальной сети
 */
@Component
public class LocalNetworkFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String remoteAddr = request.getRemoteAddr();

        if (isLocalAddress(remoteAddr)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: not from local network.");
        }
    }

    /**
     * Проверяет, что адрес принадлежит локальной сети
     * @param remoteAddr адрес
     */
    private boolean isLocalAddress(String remoteAddr) {
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteAddr);
            return inetAddress.isLoopbackAddress() ||
                    remoteAddr.startsWith("192.168.") ||
                    remoteAddr.startsWith("10.") ||
                    remoteAddr.startsWith("172.") && isInRange172(remoteAddr);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    /**
     * Проверяет, что адрес в диапазоне 172.16.0.0 - 172.31.255.255
     * @param remoteAddr адрес
     */
    private boolean isInRange172(String remoteAddr) {
        String[] parts = remoteAddr.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);
            int third = Integer.parseInt(parts[2]);
            int fourth = Integer.parseInt(parts[3]);

            if (first != 172) {
                return false;
            }

            return second >= 16 && second <= 31
                    && third >= 0 && third <= 255
                    && fourth >= 0 && fourth <= 255;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
