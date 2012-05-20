/*     */ package org.whiskcraft.ScheduledAnnouncer;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.configuration.InvalidConfigurationException;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfigurationOptions;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class Metrics
/*     */ {
/*     */   private static final int REVISION = 5;
/*     */   private static final String BASE_URL = "http://mcstats.org";
/*     */   private static final String REPORT_URL = "/report/%s";
/*     */   private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";
/*     */   private static final String CUSTOM_DATA_SEPARATOR = "~~";
/*     */   private static final int PING_INTERVAL = 10;
/*     */   private final Plugin plugin;
/* 108 */   private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
/*     */ 
/* 113 */   private final Graph defaultGraph = new Graph("Default", null);
/*     */   private final YamlConfiguration configuration;
/*     */   private final File configurationFile;
/*     */   private final String guid;
/* 133 */   private final Object optOutLock = new Object();
/*     */ 
/* 138 */   private volatile int taskId = -1;
/*     */ 
/*     */   public Metrics(Plugin plugin) throws IOException {
/* 141 */     if (plugin == null) {
/* 142 */       throw new IllegalArgumentException("Plugin cannot be null");
/*     */     }
/*     */ 
/* 145 */     this.plugin = plugin;
/*     */ 
/* 148 */     this.configurationFile = new File("plugins/PluginMetrics/config.yml");
/* 149 */     this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
/*     */ 
/* 152 */     this.configuration.addDefault("opt-out", Boolean.valueOf(false));
/* 153 */     this.configuration.addDefault("guid", UUID.randomUUID().toString());
/*     */ 
/* 156 */     if (this.configuration.get("guid", null) == null) {
/* 157 */       this.configuration.options().header("http://mcstats.org").copyDefaults(true);
/* 158 */       this.configuration.save(this.configurationFile);
/*     */     }
/*     */ 
/* 162 */     this.guid = this.configuration.getString("guid");
/*     */   }
/*     */ 
/*     */   public Graph createGraph(String name)
/*     */   {
/* 173 */     if (name == null) {
/* 174 */       throw new IllegalArgumentException("Graph name cannot be null");
/*     */     }
/*     */ 
/* 178 */     Graph graph = new Graph(name, null);
/*     */ 
/* 181 */     this.graphs.add(graph);
/*     */ 
/* 184 */     return graph;
/*     */   }
/*     */ 
/*     */   public void addCustomData(Plotter plotter)
/*     */   {
/* 193 */     if (plotter == null) {
/* 194 */       throw new IllegalArgumentException("Plotter cannot be null");
/*     */     }
/*     */ 
/* 198 */     this.defaultGraph.addPlotter(plotter);
/*     */ 
/* 201 */     this.graphs.add(this.defaultGraph);
/*     */   }
/*     */ 
/*     */   public boolean start()
/*     */   {
/* 212 */     synchronized (this.optOutLock)
/*     */     {
/* 214 */       if (isOptOut()) {
/* 215 */         return false;
/*     */       }
/*     */ 
/* 219 */       if (this.taskId >= 0) {
/* 220 */         return true;
/*     */       }
/*     */ 
/* 224 */       this.taskId = this.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, new Runnable()
/*     */       {
/* 226 */         private boolean firstPost = true;
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           try {
/* 231 */             synchronized (Metrics.this.optOutLock)
/*     */             {
/* 233 */               if ((Metrics.this.isOptOut()) && (Metrics.this.taskId > 0)) {
/* 234 */                 Metrics.this.plugin.getServer().getScheduler().cancelTask(Metrics.this.taskId);
/* 235 */                 Metrics.this.taskId = -1;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 242 */             Metrics.this.postPlugin(!this.firstPost);
/*     */ 
/* 246 */             this.firstPost = false;
/*     */           } catch (IOException e) {
/* 248 */             Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , 0L, 12000L);
/*     */ 
/* 253 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isOptOut()
/*     */   {
/* 263 */     synchronized (this.optOutLock)
/*     */     {
/*     */       try {
/* 266 */         this.configuration.load("plugins/PluginMetrics/config.yml");
/*     */       } catch (IOException ex) {
/* 268 */         Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/* 269 */         return true;
/*     */       } catch (InvalidConfigurationException ex) {
/* 271 */         Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
/* 272 */         return true;
/*     */       }
/* 274 */       return this.configuration.getBoolean("opt-out", false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enable()
/*     */     throws IOException
/*     */   {
/* 285 */     synchronized (this.optOutLock)
/*     */     {
/* 287 */       if (isOptOut()) {
/* 288 */         this.configuration.set("opt-out", Boolean.valueOf(false));
/* 289 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 293 */       if (this.taskId < 0)
/* 294 */         start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */     throws IOException
/*     */   {
/* 306 */     synchronized (this.optOutLock)
/*     */     {
/* 308 */       if (!isOptOut()) {
/* 309 */         this.configuration.set("opt-out", Boolean.valueOf(true));
/* 310 */         this.configuration.save(this.configurationFile);
/*     */       }
/*     */ 
/* 314 */       if (this.taskId > 0) {
/* 315 */         this.plugin.getServer().getScheduler().cancelTask(this.taskId);
/* 316 */         this.taskId = -1;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void postPlugin(boolean isPing)
/*     */     throws IOException
/*     */   {
/* 326 */     PluginDescriptionFile description = this.plugin.getDescription();
/*     */ 
/* 329 */     StringBuilder data = new StringBuilder();
/* 330 */     data.append(encode("guid")).append('=').append(encode(this.guid));
/* 331 */     encodeDataPair(data, "version", description.getVersion());
/* 332 */     encodeDataPair(data, "server", Bukkit.getVersion());
/* 333 */     encodeDataPair(data, "players", Integer.toString(Bukkit.getServer().getOnlinePlayers().length));
/* 334 */     encodeDataPair(data, "revision", String.valueOf(5));
/*     */ 
/* 337 */     if (isPing) {
/* 338 */       encodeDataPair(data, "ping", "true");
/*     */     }
/*     */ 
/* 343 */     synchronized (this.graphs) {
/* 344 */       Iterator iter = this.graphs.iterator();
/*     */ 
/* 346 */       while (iter.hasNext()) {
/* 347 */         Graph graph = (Graph)iter.next();
/*     */ 
/* 353 */         for (Plotter plotter : graph.getPlotters())
/*     */         {
/* 357 */           String key = String.format("C%s%s%s%s", new Object[] { "~~", graph.getName(), "~~", plotter.getColumnName() });
/*     */ 
/* 361 */           String value = Integer.toString(plotter.getValue());
/*     */ 
/* 364 */           encodeDataPair(data, key, value);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 370 */     URL url = new URL("http://mcstats.org" + String.format("/report/%s", new Object[] { encode(this.plugin.getDescription().getName()) }));
/*     */     URLConnection connection;
/*     */     URLConnection connection;
/* 377 */     if (isMineshafterPresent())
/* 378 */       connection = url.openConnection(Proxy.NO_PROXY);
/*     */     else {
/* 380 */       connection = url.openConnection();
/*     */     }
/*     */ 
/* 383 */     connection.setDoOutput(true);
/*     */ 
/* 386 */     OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
/* 387 */     writer.write(data.toString());
/* 388 */     writer.flush();
/*     */ 
/* 391 */     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 392 */     String response = reader.readLine();
/*     */ 
/* 395 */     writer.close();
/* 396 */     reader.close();
/*     */ 
/* 398 */     if ((response == null) || (response.startsWith("ERR"))) {
/* 399 */       throw new IOException(response);
/*     */     }
/*     */ 
/* 402 */     if (response.contains("OK This is your first update this hour"))
/* 403 */       synchronized (this.graphs) {
/* 404 */         Iterator iter = this.graphs.iterator();
/*     */ 
/* 406 */         while (iter.hasNext()) {
/* 407 */           Graph graph = (Graph)iter.next();
/*     */ 
/* 409 */           for (Plotter plotter : graph.getPlotters())
/* 410 */             plotter.reset();
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isMineshafterPresent()
/*     */   {
/*     */     try
/*     */     {
/* 426 */       Class.forName("mineshafter.MineServer");
/* 427 */       return true; } catch (Exception e) {
/*     */     }
/* 429 */     return false;
/*     */   }
/*     */ 
/*     */   private static void encodeDataPair(StringBuilder buffer, String key, String value)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 448 */     buffer.append('&').append(encode(key)).append('=').append(encode(value));
/*     */   }
/*     */ 
/*     */   private static String encode(String text)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 458 */     return URLEncoder.encode(text, "UTF-8");
/*     */   }
/*     */ 
/*     */   public static class Graph
/*     */   {
/*     */     private final String name;
/* 475 */     private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
/*     */ 
/*     */     private Graph(String name) {
/* 478 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 487 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void addPlotter(Metrics.Plotter plotter)
/*     */     {
/* 496 */       this.plotters.add(plotter);
/*     */     }
/*     */ 
/*     */     public void removePlotter(Metrics.Plotter plotter)
/*     */     {
/* 505 */       this.plotters.remove(plotter);
/*     */     }
/*     */ 
/*     */     public Set<Metrics.Plotter> getPlotters()
/*     */     {
/* 514 */       return Collections.unmodifiableSet(this.plotters);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 519 */       return this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 524 */       if (!(object instanceof Graph)) {
/* 525 */         return false;
/*     */       }
/*     */ 
/* 528 */       Graph graph = (Graph)object;
/* 529 */       return graph.name.equals(this.name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Plotter
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     public Plotter()
/*     */     {
/* 548 */       this("Default");
/*     */     }
/*     */ 
/*     */     public Plotter(String name)
/*     */     {
/* 557 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public abstract int getValue();
/*     */ 
/*     */     public String getColumnName()
/*     */     {
/* 573 */       return this.name;
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 584 */       return getColumnName().hashCode() + getValue();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object)
/*     */     {
/* 589 */       if (!(object instanceof Plotter)) {
/* 590 */         return false;
/*     */       }
/*     */ 
/* 593 */       Plotter plotter = (Plotter)object;
/* 594 */       return (plotter.name.equals(this.name)) && (plotter.getValue() == getValue());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\casdorph.gavin.casdorphgavin\Downloads\ScheduledAnnouncer.jar
 * Qualified Name:     org.whiskcraft.ScheduledAnnouncer.Metrics
 * JD-Core Version:    0.6.0
 */