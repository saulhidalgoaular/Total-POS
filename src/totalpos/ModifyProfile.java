package totalpos;

/* (swing1.1) */

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.tree.*;


/**
 * @version 1.1 01/15/99
 */
public class ModifyProfile extends JDialog {

    String profile;
    public boolean isOk = false;
  
  public ModifyProfile(String profileId) {
    super((JFrame)Shared.getMyMainWindows(),true);

    this.profile = profileId;

    CheckNode cn = exploreTree("/" , "root");
    if ( cn == null) {
        return;
    }
    
    JTree tree = new JTree( cn );

    tree.addKeyListener(new KeyAdapter() {
            @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            if ( evt.getKeyCode() == KeyEvent.VK_ESCAPE){
                setVisible(false);
                dispose();
            }
        }
    });

    tree.setCellRenderer(new CheckRenderer());
    tree.getSelectionModel().setSelectionMode(
      TreeSelectionModel.SINGLE_TREE_SELECTION
    );
    tree.putClientProperty("JTree.lineStyle", "Angled");
    tree.addMouseListener(new NodeSelectionListener(tree));
    JScrollPane sp = new JScrollPane(tree);

    this.setTitle("Ver/Modificar permisos de Perfil de " + profile);
    this.setSize(500, 300);
    
    getContentPane().add(sp,    BorderLayout.CENTER);
    isOk = true;
  }

  class NodeSelectionListener extends MouseAdapter {
    JTree tree;
    
    NodeSelectionListener(JTree tree) {
      this.tree = tree;
    }
    
        @Override
    public void mouseClicked(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int row = tree.getRowForLocation(x, y);
      TreePath  path = tree.getPathForRow(row);
      //TreePath  path = tree.getSelectionPath();
      if (path != null) {
        CheckNode node = (CheckNode)path.getLastPathComponent();
        boolean isSelected = ! (node.isSelected());
        node.setSelected(isSelected);
        if ( isSelected ) {
            //tree.expandPath(path);
        } else {
            //tree.collapsePath(path);
        }
        ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
        tree.revalidate();
        tree.repaint();
      }
    }
  }

      private CheckNode exploreTree(String realName , String id){
        try {
            CheckNode ans = new CheckNode(realName, profile);

            if ( !ans.isOk ) return null;
            for (Edge edge : ConnectionDrivers.listEdges(id)) {
                ans.add(exploreTree(edge.getNombre(),edge.getId()));
            }

            return ans;
        } catch (SQLException ex) {
            MessageBox msg = new MessageBox(MessageBox.SGN_DANGER, ex.getMessage(), ex);
            msg.show(this);
            return null;
        } catch (Exception ex) {
            MessageBox msb = new MessageBox(MessageBox.SGN_IMPORTANT, "Problemas al listar menu.",ex);
            msb.show(this);
            this.dispose();
            Shared.reload();
            return null;
        }
    }
}
