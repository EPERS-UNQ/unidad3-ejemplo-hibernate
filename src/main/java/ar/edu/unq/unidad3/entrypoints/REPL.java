package ar.edu.unq.unidad3.entrypoints;

import ar.edu.unq.unidad3.modelo.Item;
import ar.edu.unq.unidad3.modelo.Personaje;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernateItemDAO;
import ar.edu.unq.unidad3.persistencia.dao.impl.HibernatePersonajeDAO;
import ar.edu.unq.unidad3.service.ItemService;
import ar.edu.unq.unidad3.service.PersonajeService;
import ar.edu.unq.unidad3.service.impl.ItemServiceImpl;
import ar.edu.unq.unidad3.service.impl.PersonajeServiceImpl;
import ar.edu.unq.unidad3.service.runner.HibernateSessionFactoryProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class REPL {

    // --- help strings (fuente de verdad para help y mensajes de error) ---
    private static final String HELP_CREATE_PERSONAJE = "create Personaje -n <nombre> -hp <vida> -str <pesoMaximo>";
    private static final String HELP_CREATE_ITEM      = "create Item -n <nombre> -p <peso>";
    private static final String HELP_FIND_PERSONAJE   = "find Personaje <idPersonaje>";
    private static final String HELP_LOOT             = "loot <idPersonaje> <idItem>";
    private static final String HELP_FIND_ALL         = "find Item";
    private static final String HELP_FIND_ID          = "find Item -id <id>";
    private static final String HELP_FIND_W           = "find Item -w <peso>";
    private static final String HELP_FIND_H           = "find Item -h";
    private static final String HELP_FIND_WK          = "find Item -wk <vida>";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HibernateSessionFactoryProvider.getInstance();
        ItemService itemService = new ItemServiceImpl(new HibernateItemDAO());
        PersonajeService personajeService = new PersonajeServiceImpl(new HibernatePersonajeDAO(), new HibernateItemDAO());

        System.out.println("----------------------------------------------");
        System.out.println("- REPL | EPERS Hibernate");
        System.out.println("----------------------------------------------");
        System.out.println("- help -p para ver los comandos de personajes");
        System.out.println("- help -i para ver los comandos de items");
        System.out.println("- quit o q para cerrar el REPL");
        System.out.println("----------------------------------------------");

        for (System.out.print(">> "); sc.hasNextLine(); System.out.print(">> ")) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = tokenize(line);
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "quit", "q" -> { return; }

                    case "help" -> {
                        String flag = tokens.length > 1 ? tokens[1] : "";
                        switch (flag) {
                            case "-p" -> {
                                System.out.println(HELP_CREATE_PERSONAJE);
                                System.out.println(HELP_FIND_PERSONAJE);
                                System.out.println(HELP_LOOT);
                            }
                            case "-i" -> {
                                System.out.println(HELP_CREATE_ITEM);
                                System.out.println(HELP_FIND_ALL);
                                System.out.println(HELP_FIND_ID);
                                System.out.println(HELP_FIND_W);
                                System.out.println(HELP_FIND_H);
                                System.out.println(HELP_FIND_WK);
                            }
                            default -> System.out.println("Uso: help -p | help -i");
                        }
                    }

                    case "gm" -> {
                        System.out.println("delete");
                        System.out.println("delete Personaje");
                        System.out.println("delete Item");
                    }

                    case "create" -> {
                        if (tokens.length < 2) { System.out.println("Uso: create Personaje | create Item"); break; }
                        switch (tokens[1]) {
                            case "Personaje" -> {
                                requireFlags(tokens, HELP_CREATE_PERSONAJE, "-n", "-hp", "-str");
                                personajeService.guardarPersonaje(new Personaje(flag(tokens, "-n"), Integer.parseInt(flag(tokens, "-hp")), Integer.parseInt(flag(tokens, "-str"))));
                                System.out.println("Personaje creado.");
                            }
                            case "Item" -> {
                                requireFlags(tokens, HELP_CREATE_ITEM, "-n", "-p");
                                itemService.guardarItem(new Item(flag(tokens, "-n"), Integer.parseInt(flag(tokens, "-p"))));
                                System.out.println("Item creado.");
                            }
                            default -> System.out.println("Tipo desconocido: " + tokens[1]);
                        }
                    }

                    case "find" -> {
                        if (tokens.length < 2) { System.out.println("Uso: find Item [...] | find Personaje <id>"); break; }
                        switch (tokens[1]) {
                            case "Item" -> {
                                String flag = tokens.length > 2 ? tokens[2] : "";
                                switch (flag) {
                                    case ""    -> itemService.allItems().forEach(System.out::println);
                                    case "-h"  -> System.out.println(itemService.heaviestItem());
                                    case "-id" -> System.out.println(itemService.recuperar(Long.parseLong(argVal(tokens, 3, "-id", HELP_FIND_ID))));
                                    case "-w"  -> itemService.getMasPesados(Integer.parseInt(argVal(tokens, 3, "-w", HELP_FIND_W))).forEach(System.out::println);
                                    case "-wk" -> itemService.getItemsPersonajesDebiles(Integer.parseInt(argVal(tokens, 3, "-wk", HELP_FIND_WK))).forEach(System.out::println);
                                    default    -> System.out.println("Flag desconocido: " + flag);
                                }
                            }
                            case "Personaje" -> {
                                if (tokens.length < 3) throw new IllegalArgumentException("Falta <idPersonaje> | help: " + HELP_FIND_PERSONAJE);
                                System.out.println(personajeService.recuperarPersonaje(Long.parseLong(tokens[2])));
                            }
                            default -> System.out.println("Tipo desconocido: " + tokens[1]);
                        }
                    }

                    case "loot" -> {
                        if (tokens.length < 2) throw new IllegalArgumentException("Falta <idPersonaje> | help: " + HELP_LOOT);
                        if (tokens.length < 3) throw new IllegalArgumentException("Falta <idItem> | help: " + HELP_LOOT);
                        personajeService.recoger(Long.parseLong(tokens[1]), Long.parseLong(tokens[2]));
                        System.out.println("Item recolectado.");
                    }

                    case "delete" -> {
                        String target = tokens.length > 1 ? tokens[1] : "";
                        switch (target) {
                            case ""          -> { personajeService.eliminarTodos(); itemService.eliminarTodos(); System.out.println("Todo eliminado."); }
                            case "Personaje" -> { personajeService.eliminarTodos(); System.out.println("Personajes eliminados."); }
                            case "Item"      -> { itemService.eliminarTodos();      System.out.println("Items eliminados."); }
                            default          -> System.out.println("Uso: delete | delete Personaje | delete Item");
                        }
                    }

                    default -> System.out.println("Comando no reconocido. Usa help -p o help -i.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("[!] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[!] Error inesperado: " + e.getMessage());
            }
        }
    }

    private static void requireFlags(String[] tokens, String uso, String... required) {
        List<String> list = Arrays.asList(tokens);
        List<String> missing = new ArrayList<>();
        for (String f : required) {
            int i = list.indexOf(f);
            if (i == -1 || i + 1 >= tokens.length) missing.add(f);
        }
        if (!missing.isEmpty())
            throw new IllegalArgumentException("No se encontraron los argumentos {" + String.join(", ", missing) + "} | help: " + uso);
    }

    private static String argVal(String[] tokens, int index, String flagName, String uso) {
        if (tokens.length <= index) throw new IllegalArgumentException("Falta el valor de " + flagName + " | help: " + uso);
        return tokens[index];
    }

    private static String flag(String[] tokens, String flag) {
        List<String> list = Arrays.asList(tokens);
        return tokens[list.indexOf(flag) + 1];
    }
}
