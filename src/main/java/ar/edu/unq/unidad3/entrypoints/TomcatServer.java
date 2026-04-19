package ar.edu.unq.unidad3.entrypoints;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.persistencia.dao.ItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.PersonajeDAO;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.service.ItemService;
import ar.edu.unq.unidad3.service.PersonajeService;
import ar.edu.unq.unidad3.service.impl.ItemServiceImpl;
import ar.edu.unq.unidad3.service.impl.PersonajeServiceImpl;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TomcatServer {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat(); tomcat.setPort(8080); tomcat.getConnector(); // Init

        String contextPath = ""; String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase); // Context

        String servletName = "DispatcherServlet";
        HttpServlet dispatcherServlet = new HttpServlet() {

            private final ItemDAO itemDAO = new HibernateItemDAO();
            private final PersonajeDAO personajeDAO = new HibernatePersonajeDAO();
            private final ItemService itemService = new ItemServiceImpl(itemDAO);
            private final PersonajeService personajeService = new PersonajeServiceImpl(personajeDAO, itemDAO);

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setContentType("text/plain; charset=UTF-8");
                PrintWriter out = resp.getWriter();

                String[] parts = req.getPathInfo() != null ? req.getPathInfo().split("/") : new String[]{};
                if (parts.length < 3) {
                    resp.setStatus(400);
                    out.println("Ruta esperada: /{service}/{method}[?params]");
                    return;
                }

                String service = parts[1];
                String method  = parts[2];

                try {
                    Object result = switch (service + "/" + method) {
                        // ItemService
                        case "item/allItems"                   -> itemService.allItems();
                        case "item/heaviestItem"               -> itemService.heaviestItem();
                        case "item/getMasPesados"              -> itemService.getMasPesados(intParam(req, "peso"));
                        case "item/getItemsPersonajesDebiles"  -> itemService.getItemsPersonajesDebiles(intParam(req, "vida"));
                        // PersonajeService
                        case "personaje/recuperarPersonaje"    -> personajeService.recuperarPersonaje(longParam(req, "id"));
                        default -> { resp.setStatus(404); yield "Endpoint no encontrado: GET /" + service + "/" + method; }
                    };
                    out.println(result != null ? result.toString() : "OK");
                } catch (IllegalArgumentException e) {
                    resp.setStatus(400);
                    out.println("Parametro invalido: " + e.getMessage());
                } catch (Exception e) {
                    resp.setStatus(500);
                    out.println("Error: " + e.getMessage());
                }
            }

            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setContentType("text/plain; charset=UTF-8");
                PrintWriter out = resp.getWriter();

                String[] parts = req.getPathInfo() != null ? req.getPathInfo().split("/") : new String[]{};
                if (parts.length < 3) {
                    resp.setStatus(400);
                    out.println("Ruta esperada: /{service}/{method}[?params]");
                    return;
                }

                String service = parts[1];
                String method  = parts[2];

                try {
                    switch (service + "/" + method) {
                        // ItemService
                        case "item/guardarItem" -> {
                            Item item = new Item(strParam(req, "nombre"), intParam(req, "peso"));
                            itemService.guardarItem(item);
                            out.println("Item guardado: " + item);
                        }
                        // PersonajeService
                        case "personaje/guardarPersonaje" -> {
                            Personaje p = new Personaje(strParam(req, "nombre"), intParam(req, "vida"), intParam(req, "pesoMaximo"));
                            personajeService.guardarPersonaje(p);
                            out.println("Personaje guardado: " + p);
                        }
                        case "personaje/recoger" -> {
                            personajeService.recoger(longParam(req, "personajeId"), longParam(req, "itemId"));
                            out.println("Item recolectado");
                        }
                        default -> { resp.setStatus(404); out.println("Endpoint no encontrado: POST /" + service + "/" + method); }
                    }
                } catch (IllegalArgumentException e) {
                    resp.setStatus(400);
                    out.println("Parametro invalido: " + e.getMessage());
                } catch (Exception e) {
                    resp.setStatus(500);
                    out.println("Error: " + e.getMessage());
                }
            }

            @Override
            protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                resp.setContentType("text/plain; charset=UTF-8");
                PrintWriter out = resp.getWriter();

                String[] parts = req.getPathInfo() != null ? req.getPathInfo().split("/") : new String[]{};
                if (parts.length < 3) {
                    resp.setStatus(400);
                    out.println("Ruta esperada: /{service}/{method}");
                    return;
                }

                String service = parts[1];
                String method  = parts[2];

                try {
                    switch (service + "/" + method) {
                        case "item/eliminarTodos"      -> { itemService.eliminarTodos();      out.println("Todos los items eliminados"); }
                        case "personaje/eliminarTodos" -> { personajeService.eliminarTodos(); out.println("Todos los personajes eliminados"); }
                        default -> { resp.setStatus(404); out.println("Endpoint no encontrado: DELETE /" + service + "/" + method); }
                    }
                } catch (Exception e) {
                    resp.setStatus(500);
                    out.println("Error: " + e.getMessage());
                }
            }

            // --- helpers ---

            private String strParam(HttpServletRequest req, String name) {
                String v = req.getParameter(name);
                if (v == null) throw new IllegalArgumentException("Falta query param: " + name);
                return v;
            }

            private int intParam(HttpServletRequest req, String name) {
                return Integer.parseInt(strParam(req, name));
            }

            private long longParam(HttpServletRequest req, String name) {
                return Long.parseLong(strParam(req, name));
            }
        };

        Tomcat.addServlet(context, servletName, dispatcherServlet);
        context.addServletMappingDecoded("/*", servletName);

        tomcat.start();
        System.out.println("Tomcat iniciado en http://localhost:8080");
        tomcat.getServer().await();
    }
}
