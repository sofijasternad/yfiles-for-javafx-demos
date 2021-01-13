/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for JavaFX functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for JavaFX version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for JavaFX powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for JavaFX
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package complete.tableeditor;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.StripeTypes;
import com.yworks.yfiles.graph.Table;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.TableNodeStyle;
import com.yworks.yfiles.graph.styles.TableRenderingOrder;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventHandler;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.IReparentNodeHandler;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import com.yworks.yfiles.view.input.ReparentStripeHandler;
import com.yworks.yfiles.view.input.StripeDropInputMode;
import com.yworks.yfiles.view.input.StripeSubregion;
import com.yworks.yfiles.view.input.StripeSubregionTypes;
import com.yworks.yfiles.view.input.TableEditorInputMode;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import toolkit.CommandButton;
import toolkit.DemoApplication;
import toolkit.IconProvider;
import toolkit.TooltipProvider;
import toolkit.WebViewUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * This demo configures an instance of {@link TableEditorInputMode}
 * that is used to interactively modify the tables, as well as several child modes of
 * {@link GraphEditorInputMode} that handle context menus and tool tips.
 * Additionally, it shows how to perform a hierarchic layout that automatically respects the
 * table structure.
 * <p>
 *   Please see the demo description for further information.
 * </p>
 */
public class TableEditorDemo extends DemoApplication {
  private static final WritableImage EMPTY_IMAGE = new WritableImage(1, 1);

  public GraphControl graphControl;
  public WebView webView;
  public ListView<INode> palette;
  public ToolBar toolbar;

  // command used in the toolbar to layout the graph
  private static final ICommand RUN_LAYOUT_COMMAND = ICommand.createCommand("Run Layout");
  // the default style for group nodes
  private static final ShapeNodeStyle DEFAULT_GROUP_NODE_STYLE;
  // the default style for normal nodes
  private static final ShinyPlateNodeStyle DEFAULT_NODE_STYLE;
  // the default node size for normal nodes
  private static final SizeD DEFAULT_NODE_SIZE = new SizeD(80, 50);
  // provides graph editing capabilities
  private GraphEditorInputMode graphEditorInputMode;
  // provides table editing capabilities
  private TableEditorInputMode tableEditorInputMode;

  static {
    DEFAULT_GROUP_NODE_STYLE = new ShapeNodeStyle();
    DEFAULT_GROUP_NODE_STYLE.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    Pen pen = new Pen(Color.BLACK, 1);
    pen.setDashStyle(DashStyle.getDashDot());
    DEFAULT_GROUP_NODE_STYLE.setPen(pen);
    DEFAULT_GROUP_NODE_STYLE.setPaint(Color.TRANSPARENT);

    DEFAULT_NODE_STYLE = new ShinyPlateNodeStyle();
    DEFAULT_NODE_STYLE.setPaint(Color.ORANGE);
    DEFAULT_NODE_STYLE.setRadius(0);
  }

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph are available. Most importantly,
   * the GraphControl instance is initialized.
   */
  public void initialize() {
    WebViewUtils.initHelp(webView, this);
    // add node templates as well as column and row templates to the palette
    populatePalette();

    // configure the graph and table editing capabilities
    configureInputModes();

    // register RUN_LAYOUT as a command binding for the GraphControl
    initializeInputBindings();

    IGraph graph = graphControl.getGraph();

    // set the default styles
    initializeVisualizationDefaults(graph);

    // customize IO handling to support the styles of this demo
    initializeIO();

    // enable undo/redo support
    initializeUndoSupport(graph);

    // prevent connecting edges to table nodes and click-selecting tables to support marquee selection inside tables
    initializeTableInteraction(graph);

    // enable save/load operations
    graphControl.setFileIOEnabled(true);

    // adds the layout button to the toolbar which invokes a fitting layout algorithm for the table
    initializeLayoutButton();
  }

  /**
   * Called upon showing the stage.
   * This method initializes the graph and the input mode.
   */
  protected void onLoaded(){
    // loads a sample graph containing a table
    loadInitialGraph(graphControl);
  }

  /**
   * Creates and initializes a CommandButton that invokes a {@link HierarchicLayout}
   * with certain settings when clicked. The button is added to the toolbar.
   */
  private void initializeLayoutButton() {
    HierarchicLayout layout = new HierarchicLayout();
    layout.setOrthogonalRoutingEnabled(true);
    layout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    CommandButton commandButton = new CommandButton();
    commandButton.setCommand(RUN_LAYOUT_COMMAND);
    commandButton.setCommandParameter(layout);
    commandButton.setCommandTarget(graphControl);
    commandButton.setGraphic(IconProvider.valueOf("LAYOUT_HIERARCHIC"));
    commandButton.setTooltip(TooltipProvider.valueOf("LAYOUT_HIERARCHIC"));
    commandButton.setText("");
    toolbar.getItems().add(commandButton);
  }

  /**
   * Creates rows, columns and tables programmatically to populate the list view where the items
   * are dragged from.
   */
  private void populatePalette() {
    //Set the cell factory of the DnDList to display nodes
    palette.setCellFactory(nodeListView -> new PaletteListCell());
    palette.getItems().add(createTableNode());
    palette.getItems().add(createColumnNode());
    palette.getItems().add(createRowNode());
    palette.getItems().add(createNormalNode());
    palette.getItems().add(createGroupNode());
  }

  /**
   * Creates a table node for use as a palette template.
   */
  private INode createTableNode() {
    // binding the table is performed through a TableNodeStyle instance.
    // among other things, this also makes the table instance available in the node's lookup (use INode.lookup()...)
    TableNodeStyle tableNodeStyle = new TableNodeStyle(createTable());
    tableNodeStyle.setTableRenderingOrder(TableRenderingOrder.ROWS_FIRST);
    ShapeNodeStyle tableNodeBackgroundStyle = new ShapeNodeStyle();
    tableNodeBackgroundStyle.setPaint(Color.rgb(236, 245, 255));
    tableNodeStyle.setBackgroundStyle(tableNodeBackgroundStyle);
    SimpleNode tableNode = new SimpleNode();
    tableNode.setLayout(tableNodeStyle.getTable().getLayout());
    tableNode.setStyle(tableNodeStyle);
    return tableNode;
  }

  /**
   * Creates a table for use in table node palette templates.
   */
  private ITable createTable() {
    Table sampleTable = new Table();
    sampleTable.setInsets(new InsetsD(30, 0, 0, 0));
    // configure the defaults for the sample table
    sampleTable.getColumnDefaults().setMinimumSize(50);
    sampleTable.getRowDefaults().setMinimumSize(50);
    // setup defaults for the sample table
    // we use a custom style for the rows that alternates the stripe colors and uses a special style for all parent stripes
    AlternatingLeafStripeStyle tableStyle = new AlternatingLeafStripeStyle(
        new StripeDescriptor(Color.rgb(196, 215, 237), Color.rgb(196, 215, 237)),
        new StripeDescriptor(Color.rgb(171, 200, 226), Color.rgb(171, 200, 226)),
        new StripeDescriptor(Color.rgb(113, 146, 178), Color.rgb(113, 146, 178)));
    sampleTable.getRowDefaults().setStyle(tableStyle);
    // we use a simpler style for the columns
    StripeDescriptor descriptor = new StripeDescriptor(Color.TRANSPARENT, Color.rgb(113, 146, 178));
    sampleTable.getColumnDefaults().setStyle(new AlternatingLeafStripeStyle(descriptor));
    // create a row and a column in the sample table
    sampleTable.createGrid(1, 1);
    // use twice the default width for this sample column (looks nicer in the preview...)
    IColumn firstCol = first(sampleTable.getColumns());
    sampleTable.setSize(firstCol, firstCol.getActualSize() * 2);
    return sampleTable;
  }

  /**
   * Creates a table node for use as a palette template for new table columns.
   */
  private INode createColumnNode() {
    // create a sample table that displays a single columns
    ITable columnTable = createColumnTable();
    TableNodeStyle columnSampleNodeStyle = new TableNodeStyle(columnTable);
    // the single-column table is bound to a node to have only objects of one
    // type in the palette
    SimpleNode columnNode = new SimpleNode();
    columnNode.setLayout(columnTable.getLayout());
    columnNode.setStyle(columnSampleNodeStyle);
    // provide easy access to the sample column through the node's tag
    columnNode.setTag(first(columnTable.getRootColumn().getChildColumns()));
    return columnNode;
  }

  /**
   * Creates a sample table that displays a single column for use in
   * nodes that are palette templates.
   */
  private ITable createColumnTable() {
    ITable columnSampleTable = new Table();
    // create the sample column by specifying the desired visualization
    StripeDescriptor descriptor = new StripeDescriptor(Color.rgb(171, 200, 226), Color.rgb(240, 248, 255));
    IColumn columnSampleColumn = columnSampleTable.createColumn(200d);
    columnSampleTable.setStyle(columnSampleColumn, new AlternatingLeafStripeStyle(descriptor));
    // create an invisible sample row in this table, otherwise only the column
    // header is displayed
    IRow columnSampleRow = columnSampleTable.createRow(200d);
    columnSampleTable.setStyle(columnSampleRow, VoidStripeStyle.INSTANCE);
    columnSampleTable.setStripeInsets(columnSampleRow, InsetsD.EMPTY);

    columnSampleTable.addLabel(columnSampleColumn, "Column");
    return columnSampleTable;
  }

  /**
   * Creates a table node for use as a palette template for new table rows.
   */
  private INode createRowNode() {
    ITable rowTable = createRowTable();
    TableNodeStyle rowNodeStyle = new TableNodeStyle(rowTable);
    SimpleNode rowNode = new SimpleNode();
    rowNode.setLayout(rowTable.getLayout());
    rowNode.setStyle(rowNodeStyle);
    rowNode.setTag(first(rowTable.getRootRow().getChildRows()));
    return rowNode;
  }

  /**
   * Creates a sample table that displays a single row for use in
   * nodes that are palette templates.
   */
  private ITable createRowTable() {
    ITable rowSampleTable = new Table();

    // create the sample row by configuring visualization defaults for rows
    StripeDescriptor descriptor = new StripeDescriptor(Color.rgb(171, 200, 226), Color.rgb(240, 248, 255));
    rowSampleTable.getRowDefaults().setStyle(new AlternatingLeafStripeStyle(descriptor, descriptor, descriptor));
    IRow rowSampleRow = rowSampleTable.createRow();

    // create an invisible sample column in this table, otherwise only the row
    // header is displayed
    IColumn rowSampleColumn = rowSampleTable.createColumn(200d);
    rowSampleTable.setStyle(rowSampleColumn, VoidStripeStyle.INSTANCE);
    rowSampleTable.setStripeInsets(rowSampleColumn, InsetsD.EMPTY);

    rowSampleTable.addLabel(rowSampleRow, "Row");
    return rowSampleTable;
  }

  /**
   * Creates a group node for use as a palette template.
   */
  private INode createGroupNode() {
    SimpleNode groupNode = new SimpleNode();
    groupNode.setStyle(DEFAULT_GROUP_NODE_STYLE);
    groupNode.setLayout(new RectD(PointD.ORIGIN, DEFAULT_NODE_SIZE));
    // set a custom tag that identifies this node as group node
    // this tag is used in NodeDropInputMode's isGroupNodePredicate
    // to determine whether a group node or a normal node should be
    // created when dropping a node template from the palette
    groupNode.setTag("GroupNode");
    return groupNode;
  }

  /**
   * Creates a normal node for use as a palette template.
   */
  private INode createNormalNode() {
    SimpleNode normalNode = new SimpleNode();
    normalNode.setStyle(DEFAULT_NODE_STYLE);
    normalNode.setLayout(new RectD(PointD.ORIGIN, DEFAULT_NODE_SIZE));
    return normalNode;
  }

  /**
   * Returns the first element in the given iterable.
   * @throws java.util.NoSuchElementException if there are no elements
   * in the given iterable.
   */
  private static <T> T first(IEnumerable<T> enumerable) {
    return enumerable.stream().findFirst().orElseThrow(NoSuchElementException::new);
  }

  /**
   * Configures the main input mode and creates a {@link GraphEditorInputMode}.
   */
  private void configureInputModes() {
    graphEditorInputMode = new TableGraphEditorInputMode();

    // we want orthogonal edge editing/creation
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(true);
    graphEditorInputMode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // activate drag and drop from the style palette
    NodeDropInputMode nodeDropInputMode = new MyNodeDropInputMode();
    nodeDropInputMode.setPriority(70);
    nodeDropInputMode.setPreviewEnabled(true);
    nodeDropInputMode.setEnabled(true);
    // we identify the group nodes during a drag by either a custom tag or if they have a table associated
    nodeDropInputMode.setIsGroupNodePredicate(this::isGroupNode);
    graphEditorInputMode.setNodeDropInputMode(nodeDropInputMode);

    // disable node creation on click - new nodes have to be created using
    // drag and drop form the palette
    graphEditorInputMode.setCreateNodeAllowed(false);

    //Register custom re-parent handler that prevents re-parenting of table nodes (i.e. they may only appear on root level)
    graphEditorInputMode.setReparentNodeHandler(new MyReparentHandler(graphEditorInputMode.getReparentNodeHandler()));

    // enable interactive grouping operations
    graphEditorInputMode.setGroupingOperationsAllowed(true);

    configureTableEditing();

    graphControl.setInputMode(graphEditorInputMode);
  }

  /**
   * Determines whether a node is a group node.
   */
  private boolean isGroupNode(INode node) {
    return isTableNode(node) || "GroupNode".equals(node.getTag());
  }

  /**
   * Configures table editing specific parts.
   */
  private void configureTableEditing() {
    tableEditorInputMode = new TableEditorInputMode();

    // enable drag and drop
    tableEditorInputMode.getStripeDropInputMode().setEnabled(true);
    // restrict the nesting depths of columns and rows
    // i.e. only top-level columns may have child columns; same for rows
    ReparentStripeHandler reparentStripeHandler = new ReparentStripeHandler();
    reparentStripeHandler.setMaxColumnLevel(2);
    reparentStripeHandler.setMaxRowLevel(2);
    tableEditorInputMode.setReparentStripeHandler(reparentStripeHandler);

    // add to GraphEditorInputMode - we set the priority lower than for the handle input mode so that handles win if
    // both gestures are possible
    tableEditorInputMode.setPriority(graphEditorInputMode.getHandleInputMode().getPriority() + 1);
    graphEditorInputMode.add(tableEditorInputMode);

    graphEditorInputMode.setContextMenuItems(GraphItemTypes.NODE);
    graphEditorInputMode.addPopulateItemContextMenuListener(new PopulateItemContextMenuHandler());
    graphEditorInputMode.addPopulateItemContextMenuListener(new PopulateNodeContextMenuHandler());

    // set up tooltips to appear when hovering over a column or row header
    graphEditorInputMode.getMouseHoverInputMode().addQueryToolTipListener(new QueryToolTipHandler());
  }

  /**
   * Registers an input binding for running the layout algorithm used to arrange
   * the demo's graph.
   */
  private void initializeInputBindings() {
    graphEditorInputMode.getKeyboardInputMode().addCommandBinding(RUN_LAYOUT_COMMAND, this::executeLayout, this::canExecuteLayout);
  }

  /**
   * Retrieves the column or row at the specified location.
   */
  private StripeSubregion getStripe(PointD location) {
    return tableEditorInputMode.findStripe(location, StripeTypes.ALL, StripeSubregionTypes.HEADER);
  }

  /**
   * Sets the default styles of the normal and group nodes.
   */
  private void initializeVisualizationDefaults(IGraph graph) {
    // set defaults for normal nodes
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.setStyle(DEFAULT_NODE_STYLE);
    nodeDefaults.setSize(DEFAULT_NODE_SIZE);

    // set defaults for group nodes
    INodeDefaults groupNodeDefaults = graph.getGroupNodeDefaults();
    groupNodeDefaults.setStyle(DEFAULT_GROUP_NODE_STYLE);
    groupNodeDefaults.setSize(DEFAULT_NODE_SIZE);
  }

  /**
   * Customizes IO handling to support the styles of this demo.
   */
  private void initializeIO() {
    // create an IOHandler that will be used for all IO operations
    GraphMLIOHandler ioh = new GraphMLIOHandler();

    // we set the IO handler on the GraphControl, so the GraphControl's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphControl.setGraphMLIOHandler(ioh);

    // add namespace for styles of this demo
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-javafx/demos/TableEditor/1.0", "complete.tableeditor", getClass().getClassLoader());
  }

  /**
   * Loads a sample graph.
   */
  private void loadInitialGraph(GraphControl graphControl) {
    try {
      graphControl.importFromGraphML(getClass().getResource("resources/sample.graphml").toExternalForm());
    } catch (IOException e) {
      e.printStackTrace();
    }
    graphControl.fitGraphBounds();
  }

  /**
   * Enables undo/redo support.
   */
  private void initializeUndoSupport(IGraph graph) {
    // configure Undo...
    // enable general undo support
    graph.setUndoEngineEnabled(true);
    // use the undo support from the graph also for all future table instances
    Table.installStaticUndoSupport(graph);
  }

  /**
   * Prevents connecting edges to table nodes and click-selecting tables to
   * support marquee selection inside tables.
   */
  private void initializeTableInteraction(IGraph graph) {
    NodeDecorator nodeDecorator = graph.getDecorator().getNodeDecorator();

    // provide no candidates for edge creation at table nodes - this effectively disables edge creations for those nodes
    Predicate<INode> nodeTablePredicate = TableEditorDemo::isTableNode;
    nodeDecorator.getPortCandidateProviderDecorator().setImplementation(nodeTablePredicate, IPortCandidateProvider.NO_CANDIDATES);

    // customize marquee selection handling for table nodes
    nodeDecorator.getMarqueeTestableDecorator().setFactory(nodeTablePredicate, node -> new TableNodeMarqueeTestable(node.getLayout()));
  }

  /**
   * Determines whether or not the given item is associated to an
   * {@link com.yworks.yfiles.graph.ITable} instance.
   * @param item the item to check.
   * @return <code>true</code> if the given item is associated to an
   * {@link com.yworks.yfiles.graph.ITable} instance; <code>false</code>
   * otherwise.
   */
  private static boolean isTableNode(ILookup item) {
    return item.lookup(ITable.class) != null;
  }

  /**
   * A custom ListCell implementation that triggers the actual Drag and Drop events.
   */
  private class PaletteListCell extends ListCell<INode> {

    /**
     * The node that this cell renders
     */
    private INode node = null;

    PaletteListCell() {

      /*
      When the user starts dragging a node in the ListView, setup the
      NodeDefaults of the GraphControl and the information about the dragging.
      */
      setOnDragDetected(event -> {
        // start the drag
        Dragboard db = getListView().startDragAndDrop(TransferMode.ANY);
        Map<DataFormat, Object> contentMap = new HashMap<>();

        // the index of the INodes in styleListBox shall be used as drop id and put into the DragBoard. Therefore we
        // have to map the index to the INode instance in the dropDataMap of the appropriate DropInputMode later
        Integer id1 = palette.getSelectionModel().selectedIndexProperty().intValue();

        if (node.getTag() instanceof IStripe){
          // If the dummy node has a stripe as its tag, we use the stripe directly
          // This allows StripeDropInputMode to take over
          tableEditorInputMode.getStripeDropInputMode().getDropDataMap().put(id1, node.getTag());

          // use the selected index as content.
          contentMap.put(StripeDropInputMode.DATA_FORMAT_DROP_ID, id1);

        } else {

          // Otherwise, we use a copy of the node and let (hopefully) NodeDropInputMode take over
          SimpleNode value = new SimpleNode();
          value.setLayout(node.getLayout());
          value.setStyle((INodeStyle) node.getStyle().clone());
          value.setTag(node.getTag());
          graphEditorInputMode.getNodeDropInputMode().getDropDataMap().put(id1, value);

          // use the selected index as content.
          contentMap.put(NodeDropInputMode.DATA_FORMAT_DROP_ID, id1);

        }

        db.setContent(contentMap);
        // to prevent a semi-transparent paper appears above the dragged node on MacOSX,
        // we set the drag view to a blank image
        db.setDragView(EMPTY_IMAGE);
        event.consume();
      });

      setOnDragDone(Event::consume);

      // remove the "stripes" of the default list view style
      setStyle("-fx-background-color: transparent");
    }

    /**
     * Creates the visual representation of a node in the ListView
     */
    @Override
    public void updateItem(INode item, boolean empty) {
      super.updateItem(item, empty);

      // remember the node for this cell
      if (node == null){
        node = item;
      }

      if (empty) {
        // what to do if the cell has no item
        setGraphic(null);
        setText(null);
      } else {
        // create the visual representation using the node's style renderer
        INodeStyle style = item.getStyle();
        Node fxNode = style.getRenderer().getVisualCreator(item, style).createVisual(graphControl.createRenderContext());
        if (fxNode instanceof VisualGroup) {
          VisualGroup container = (VisualGroup) fxNode;
          // necessary to be shown in the ListView
          container.setManaged(true);
        }

        // put the node into a pane to center it
        StackPane pane = new StackPane();
        pane.getChildren().add(fxNode);
        setGraphic(pane);
        setText(null);
      }
    }
  }

  /**
   * Prevents click-selection of table nodes to support marquee selection inside
   * tables.
   */
  private static class TableGraphEditorInputMode extends GraphEditorInputMode {
    /**
     * Prevents click-selection of table nodes to support marquee selection inside
     * tables.
     */
    @Override
    protected boolean shouldClickSelect(IModelItem item) {
      return !isTableNode(item) && super.shouldClickSelect(item);
    }
  }

  /**
   * Prevents dropping table nodes inside existing group (or table) nodes.
   */
  private static class MyNodeDropInputMode extends NodeDropInputMode {
    /**
     * Prevents dropping table nodes inside existing group (or table) nodes.
     */
    @Override
    protected IModelItem getDropTarget(PointD dragLocation) {
      return isTableNode(getDraggedItem()) ? null : super.getDropTarget(dragLocation);
    }
  }

  /**
   * Prevents table nodes from being moved into other group (or table) nodes.
   */
  private static class MyReparentHandler implements IReparentNodeHandler {

    private IReparentNodeHandler wrappedReparentHandler;

    public MyReparentHandler(IReparentNodeHandler wrappedReparentHandler) {
      this.wrappedReparentHandler = wrappedReparentHandler;
    }

    @Override
    public boolean isReparentGesture(IInputModeContext context, INode node) {
      return wrappedReparentHandler.isReparentGesture(context, node);
    }

    /**
     * Prevents table nodes from being moved into other group (or table) nodes.
     */
    @Override
    public boolean shouldReparent(IInputModeContext context, INode node) {
      return !isTableNode(node) && wrappedReparentHandler.shouldReparent(context, node);
    }

    @Override
    public boolean isValidParent(IInputModeContext context, INode node, INode newParent) {
      return wrappedReparentHandler.isReparentGesture(context, node);
    }

    @Override
    public void reparent(IInputModeContext context, INode node, INode newParent) {
      wrappedReparentHandler.reparent(context, node, newParent);
    }
  }

  /**
   * Event handler for the context menu for stripe header. We show only a simple context menu that demonstrates the
   * {@link TableEditorInputMode#insertChild(com.yworks.yfiles.graph.IStripe, int)}> convenience method.
   */
  private class PopulateItemContextMenuHandler implements IEventHandler<PopulateItemContextMenuEventArgs<IModelItem>> {
    @Override
    public void onEvent(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
      if (!args.isHandled()) {
        StripeSubregion stripe = getStripe(args.getQueryLocation());
        if (stripe != null) {
          ContextMenu contextMenu = (ContextMenu) args.getMenu();

          final MenuItem deleteItem = new MenuItem("Delete " + stripe.getStripe());
          deleteItem.setOnAction(actionEvent -> ICommand.DELETE.execute(stripe.getStripe(), graphControl));
          contextMenu.getItems().add(deleteItem);

          final MenuItem insertBeforeItem = new MenuItem("Insert new stripe before " + stripe.getStripe());
          insertBeforeItem.setOnAction(actionEvent -> {
            final IStripe parent = stripe.getStripe().getParentStripe();
            final int index = stripe.getStripe().getIndex();
            tableEditorInputMode.insertChild(parent, index);
          });
          contextMenu.getItems().add(insertBeforeItem);

          final MenuItem insertAfterItem = new MenuItem("Insert new stripe after " + stripe.getStripe());
          insertAfterItem.setOnAction(actionEvent -> {
            final IStripe parent = stripe.getStripe().getParentStripe();
            final int index = stripe.getStripe().getIndex();
            tableEditorInputMode.insertChild(parent, index + 1);
          });
          contextMenu.getItems().add(insertAfterItem);
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Event handler for tool tips over a stripe header
   * We show only tool tips for stripe headers in this demo.
   */
  private class QueryToolTipHandler implements IEventHandler<ToolTipQueryEventArgs> {
    @Override
    public void onEvent(Object source, ToolTipQueryEventArgs args) {
      if (!args.isHandled()){
        StripeSubregion stripeDescriptor = getStripe(args.getQueryLocation());
        if (stripeDescriptor != null) {
          // pass the stripes title to the tooltip
          args.setToolTip(new Tooltip(stripeDescriptor.getStripe().toString()));
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Event handler for the context menu for hits on a node. We show only a dummy context menu to demonstrate the basic
   * principle.
   */
  private class PopulateNodeContextMenuHandler implements IEventHandler<PopulateItemContextMenuEventArgs<IModelItem>> {
    @Override
    public void onEvent(Object source, PopulateItemContextMenuEventArgs<IModelItem> args) {
      if (!args.isHandled()) {
        Predicate<IModelItem> predicate = TableEditorDemo::isTableNode;
        Iterator<IModelItem> items = graphEditorInputMode.findItems(args.getContext(), args.getQueryLocation(),
            new GraphItemTypes[]{GraphItemTypes.NODE}, predicate).iterator();
        IModelItem tableNode = items.hasNext() ? items.next() : null;
        if (tableNode != null) {
          ContextMenu contextMenu = (ContextMenu) args.getMenu();
          final MenuItem cutItem = new MenuItem("ContextMenu for " + tableNode);
          contextMenu.getItems().add(cutItem);
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Customizes marquee selection so that a table node gets selected only if
   * its bounds are completely enclosed by the marquee rectangle.
   */
  private static class TableNodeMarqueeTestable implements IMarqueeTestable {
    private IRectangle layout;

    TableNodeMarqueeTestable(IRectangle layout) {
      this.layout = layout;
    }

    @Override
    public boolean isInBox(IInputModeContext context, RectD box) {
      return box.contains(layout.getTopLeft()) && box.contains(layout.toRectD().getBottomRight());
    }
  }

  /**
   * Determines whether the {@link #RUN_LAYOUT_COMMAND} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object param, Object sender) {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithms that currently calculates
    //   a layout may result in errors
    if (param instanceof ILayoutAlgorithm && !graphEditorInputMode.getWaitInputMode().isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphControl.getGraph();
     return graph != null && !(graph.getNodes().size() == 0);
    } else {
      return false;
    }
  }

  /**
   * Handles the {@link #RUN_LAYOUT_COMMAND}.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object source) {
    if (parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;
      graphControl.morphLayout(layout, Duration.ofMillis(500));
      return true;
    }
    return false;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
