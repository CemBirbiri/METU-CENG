import java.util.ArrayList;
public class LibraryTree {
	
	public LibraryNode primaryRoot;		//root of the primary B+ tree
	public LibraryNode secondaryRoot;	//root of the secondary B+ tree
	public LibraryTree(Integer order)
	{
		LibraryNode.order = order;
		primaryRoot = new LibraryNodeLeaf(null);
		primaryRoot.level = 0;
		secondaryRoot = new LibraryNodeLeaf(null);
		secondaryRoot.level = 0;
	}
	
	public void add_leaf(LibraryNodeLeaf current,CengBook book){
		//bu fonksiyonun return type ı  LibraryNodePrimaryIndex-> nedeni overflow olma durumunda yukarıya 
		//bir tane LibraryNodePrimaryIndex çıkartması lazım.
		//overflow olmazsa null dönsün
			
		

		if(2*current.order == current.bookCount()){
	

			int i=0;
			while(i< current.bookCount()){ //leafte dogru yere ekliyor
				if(current.bookKeyAtIndex(i) < book.key()){
					i++;}
				else break;
					
			}
			((LibraryNodeLeaf) current).getbooks().add(i, book);

			
			i=0;
			LibraryNode left_sibling= new LibraryNodeLeaf(current.getParent() ); //left sibling olustu
			LibraryNode right_sibling= new LibraryNodeLeaf(current.getParent() ); //right sibling olustu
			
			i=0;
				while(i< current.order){ //adding left sibling
					((LibraryNodeLeaf) left_sibling).getbooks().add(i,current.bookAtIndex(i));
					
					i++;
				}
			int j=current.order;
				 i=0;
				while(j < 2*current.order+1){ //adding right sibling(right sibling doluyor)
					
					((LibraryNodeLeaf) right_sibling).getbooks().add(i,current.bookAtIndex(j));
					i++;
					j++;
				}

				//current.setParent(null);//split edilmiş node'un artık parenti null
				

				


			if(((LibraryNodeLeaf) current).getParent()==null){ //burda yeni bir internal node olusuyor. left ve right sibling baglanıyor bu nodea
				

				LibraryNode parent= new  LibraryNodePrimaryIndex(null);
				parent.setParent(null);
				primaryRoot=parent;
				((LibraryNodePrimaryIndex) parent).getAllKeys().add(0, ((LibraryNodeLeaf) right_sibling).bookKeyAtIndex(0));
				
				((LibraryNodeLeaf) left_sibling).setParent(parent);
				
				((LibraryNodeLeaf) right_sibling).setParent(parent);
				
				((LibraryNodePrimaryIndex) parent).getAllChildren().add(0,left_sibling);
				((LibraryNodePrimaryIndex) parent).getAllChildren().add(1,right_sibling);
				((LibraryNodeLeaf) current).setParent(null);


				


			}
			else{ //leaf split ediliyor. parentına middle key gidiyor
					//eger parent ları null değilse , rigthin middle keyi parenta cıkacak .
				//sonra parent overflow yasarsa split_internal functinına gidecek
				((LibraryNodeLeaf) left_sibling).setParent(current.getParent());
				
				((LibraryNodeLeaf) right_sibling).setParent(current.getParent());
			
				i=0; 
				while(i< ((LibraryNodePrimaryIndex) (current .getParent())).keyCount() ){
					if(((LibraryNodeLeaf) current).bookKeyAtIndex(0) > ((LibraryNodePrimaryIndex) (current .getParent())).keyAtIndex(i) ){
						
						i++;
					}//parentın childrenlarından rootu siliyoruz
					else break;
				}
			
					
					if(((LibraryNodeLeaf) current).bookKeyAtIndex(0)  <  ((LibraryNodePrimaryIndex) (current .getParent())).keyAtIndex(i)){
						((LibraryNodePrimaryIndex) (current .getParent())) .getAllChildren().remove(  i  );
					}
					
				else ((LibraryNodePrimaryIndex) (current .getParent())) .getAllChildren().remove(  i+1  );
				

				

				/*i=0;
				while(i< ((LibraryNodePrimaryIndex) (current.getParent())).keyCount() ){
					System.out.println("lan");
					if(((LibraryNodeLeaf) current).bookKeyAtIndex(0)  ==  ((LibraryNodePrimaryIndex) (current.getParent())) . keyAtIndex(i)) {
						System.out.println("lön");
						break;}
					else i++;
				}
				((LibraryNodePrimaryIndex) (current .getParent())) .getAllChildren().remove(  i+1  );*/

				current.setParent(null);

				
				 i=0;// right siblingin middle keyi up'lanıyor, roota middle key ekleniyor.
				while(i< ((LibraryNodePrimaryIndex) (left_sibling .getParent())).keyCount() ){
					if(  ((LibraryNodeLeaf) right_sibling).bookKeyAtIndex(0) > ((LibraryNodePrimaryIndex) (left_sibling .getParent())) . keyAtIndex(i)      ){
						i++;}
					else break;
				}
				((LibraryNodePrimaryIndex)  left_sibling .getParent()) .getAllKeys().add(i,((LibraryNodeLeaf)right_sibling).bookKeyAtIndex(0));//roota middle key ekleniyor.
				((LibraryNodePrimaryIndex) (left_sibling .getParent())) .getAllChildren().add(i,left_sibling);
				((LibraryNodePrimaryIndex) (left_sibling .getParent())) .getAllChildren().add(i+1,right_sibling);



				if(is_full_internal(  (  (LibraryNodePrimaryIndex) (left_sibling .getParent()  )  )  ) ==1    )
				{
					
					split_internal(  ((LibraryNodePrimaryIndex) (left_sibling .getParent())  )    );
				}
			}

		
	}
		else { 
				//bu durumda overflow olmamıs . sadece leaf node a yeni key/book ekliyorsun

			int i=0;
			
			while(i< current.bookCount()){
				if(current.bookKeyAtIndex(i) < book.key()) i++;
				else break;
			}
			((LibraryNodeLeaf) current).getbooks().add(i, book);

				
		}

	}
	public void split_internal(  LibraryNodePrimaryIndex root   ){//bu fonksiyona overflow olmuşinternal node geliyor
		
		

		LibraryNode left_sibling = new  LibraryNodePrimaryIndex(root.getParent());
		LibraryNode right_sibling = new  LibraryNodePrimaryIndex(root.getParent());
		int i=0;
		while(i< root.order){ //adding left sibling
			((LibraryNodePrimaryIndex) left_sibling).getAllKeys().add(i,root.keyAtIndex(i));
			
			i++;
		}
		i=0;
		while(i< ((LibraryNodePrimaryIndex) left_sibling).keyCount()+1){ //adding children to left sibling
			
			((LibraryNodePrimaryIndex) left_sibling).getAllChildren().add(i,root.getChildrenAt(i));
			root.getChildrenAt(i).setParent(left_sibling);// burda rootun cocuklarının parenti left_sibling olsun diyorum
			((LibraryNodePrimaryIndex) left_sibling).getChildrenAt(i).setParent(left_sibling);//burda işi garantiliyorum
			
			i++;
		}


		
		//((LibraryNodePrimaryIndex) left_sibling).getAllChildren().add(i,root.getChildrenAt(i));//son child ekleme
		//root.getChildrenAt(i).setParent(left_sibling);
		
		int j=root.order+1;
		 i=0;
		while(j< 2*root.order+1){ //adding right sibling(right sibling doluyor)
			((LibraryNodePrimaryIndex) right_sibling).getAllKeys().add(i,root.keyAtIndex(j));
			
			i++;
			j++;
		}
		j=root.order+1;
		 i=0;
		while(j< 2*root.order+2){ ////adding children to right sibling
			
			((LibraryNodePrimaryIndex) right_sibling).getAllChildren().add(i,root.getChildrenAt(j));
			root.getChildrenAt(j).setParent(right_sibling);// burda rootun cocuklarının parenti right_sibling olsun diyorum
			((LibraryNodePrimaryIndex) right_sibling).getChildrenAt(i).setParent(right_sibling);//burda işi garantiliyorum
			
			i++;
			j++;
		}

//burda root hala var kurtul ondan!
	
//*********************************|****************emin değilim
		//                         v
	if(((LibraryNodePrimaryIndex) root).getParent()!=null){

		
				i=0; 
				while(i< ((LibraryNodePrimaryIndex) (root .getParent())).keyCount() ){
					if(root.keyAtIndex(0) > ((LibraryNodePrimaryIndex) (root .getParent())).keyAtIndex(i) ){
						i++;
					}//parentın childrenlarından rootu siliyoruz
					else break;
				}
				((LibraryNodePrimaryIndex) (root .getParent())) .getAllChildren().remove(  i  );
				
				 i=0;// right siblingin middle keyi up'lanıyor, roota middle key ekleniyor.
				while(i< ((LibraryNodePrimaryIndex) (root .getParent())).keyCount() ){
					if(  ((LibraryNodePrimaryIndex) root).keyAtIndex(root.order) > ((LibraryNodePrimaryIndex) (root .getParent())) . keyAtIndex(i)      ){
						i++;}
					else break;
				}
				((LibraryNodePrimaryIndex) (root .getParent())) .getAllKeys().add(i,((LibraryNodePrimaryIndex) root).keyAtIndex(root.order));//roota middle key ekleniyor.
				((LibraryNodePrimaryIndex) (root .getParent())) .getAllChildren().add(i,left_sibling);
				((LibraryNodePrimaryIndex) (root .getParent())) .getAllChildren().add(i+1,right_sibling);

				((LibraryNodePrimaryIndex) left_sibling).setParent(root.getParent());
				((LibraryNodePrimaryIndex) right_sibling).setParent(root.getParent());

				root.setParent(null);

				
				 

				
				if(is_full_internal(  (  (LibraryNodePrimaryIndex) (left_sibling .getParent()  )  )  ) ==1    )
				{
					split_internal(  ((LibraryNodePrimaryIndex) (left_sibling .getParent())  )    );
				}
		
	}
	else{// gelen rootun parenti null ise yeni internal node olustur. left ve rigthi buna bagla
	
		
			LibraryNode parent= new  LibraryNodePrimaryIndex(null);
			primaryRoot=parent;
				((LibraryNodePrimaryIndex) parent).getAllKeys().add(0, ((LibraryNodePrimaryIndex) root).keyAtIndex(root.order));
				
				((LibraryNodePrimaryIndex) left_sibling).setParent(parent);
				
				((LibraryNodePrimaryIndex) right_sibling).setParent(parent);

				
				((LibraryNodePrimaryIndex) parent).getAllChildren().add(0,left_sibling);
				((LibraryNodePrimaryIndex) parent).getAllChildren().add(1,right_sibling);

			root.setParent(null);
			
			
			root.getAllChildren().clear();
			primaryRoot=parent;
		
	}

	

	}
	public Integer is_full_internal(LibraryNodePrimaryIndex node){
		if( 2* node.order < node.keyCount()) return 1;
		return 0;

	}
	public void add_internal(LibraryNodePrimaryIndex root,CengBook book) {
		LibraryNode current;
			int i=0;  
				 
				while(i< ((LibraryNodePrimaryIndex) root).keyCount()-1 && book.key() > ((LibraryNodePrimaryIndex) root).keyAtIndex(i))
				{
					i++;}//gelen internal node'u cocuklarına bakarak ,book.key()'inhangi cocuk old buluyor					
				
				if(book.key() >= ((LibraryNodePrimaryIndex) root).keyAtIndex(i)){ //sag cocuk
					
					 current=  ((LibraryNodePrimaryIndex) root).getChildrenAt(i+1);
				}
				else {  current = ((LibraryNodePrimaryIndex) root).getChildrenAt(i);
				}//sol cocuk

				if((current).getType() ==LibraryNodeType.Leaf){ //type'ıleaf ise add_leafegönder
							
						if(is_full_internal(  (  (LibraryNodePrimaryIndex) (current .getParent()  )  )  ) ==1    )
						{
							
							split_internal(  ((LibraryNodePrimaryIndex) (current .getParent())  )    );
						}

			

					
					add_leaf(((LibraryNodeLeaf) current),book);
				}
				else {
					
				add_internal(((LibraryNodePrimaryIndex)current),book);//type'ı internal ise add_internala tekrar gönder taki leafe gelene kadar
				}
		
		

	
		
}
	public void addBook(CengBook book) {
		//algoritma burdan basliyor. Rootun type ına bakiyor. Leaf ise add_leaf e ;
		//internal nodesa add_internala gönderiyor
		
		if(primaryRoot.getType()== LibraryNodeType.Leaf) { //add leafe gönderiyor

			
			add_leaf(((LibraryNodeLeaf)primaryRoot) , book);
		}
			
		else{
			
			add_internal(((LibraryNodePrimaryIndex) primaryRoot),book); //add_internala gönderiyor
			
		}
			

	} 
	
	

	
	
	
	// Extra functions if needed





public CengBook searchBook_leaf(LibraryNodeLeaf current,Integer key){
	
		int i=0;
		while(i< ((LibraryNodeLeaf) current).bookCount()){
			
			
			if(current.bookKeyAtIndex(i)==key){
				
				

				System.out.println(current.bookAtIndex(i).fullName());

				break;
			}
			i++;
		}
		return null;
	}

	public CengBook searchBook(Integer key) {
			LibraryNode current= primaryRoot;
			
			if(current.getType()== LibraryNodeType.Leaf) {

				
				searchBook_leaf(((LibraryNodeLeaf) current),key);}
			
			else {
				
				searchBook_internal(primaryRoot,key);}
			
			return null;
	}
public CengBook searchBook_internal(LibraryNode root,Integer key) {
		LibraryNode current;
			int i=0;  //primaryRoot InternalNode old dusunerek
				 print_Node( root);
				while(i< ((LibraryNodePrimaryIndex) root).keyCount()-1 && key > ((LibraryNodePrimaryIndex) root).keyAtIndex(i))
				{
					i++;}
					
			
				if(key >= ((LibraryNodePrimaryIndex) root).keyAtIndex(i)){

					
					 current=  ((LibraryNodePrimaryIndex) root).getChildrenAt(i+1);
				}
				else {  
					
					current = ((LibraryNodePrimaryIndex) root).getChildrenAt(i);}

				if(
					current.getType() ==LibraryNodeType.Leaf){


					
					searchBook_leaf(((LibraryNodeLeaf) current),key);
				}
				else {
					
					searchBook_internal(((LibraryNodePrimaryIndex)current),key);}
		
		

	
		return null;
}
	
	public void print_Node(LibraryNode root){

		
		int i=0;
		while(i< ((LibraryNodePrimaryIndex) root).keyCount()){
			System.out.println(((LibraryNodePrimaryIndex) root).keyAtIndex(i));
			i++;
		}

	}

public void print_internal(LibraryNodePrimaryIndex root){
				
		
		int i=0;
		while(i< ((LibraryNodePrimaryIndex) root).keyCount()){
			System.out.println(((LibraryNodePrimaryIndex) root).keyAtIndex(i));
			i++;
		}
		

		if(((LibraryNodePrimaryIndex) root). getChildrenAt(0).getType()==LibraryNodeType.Leaf){
			i=0;
			while(i< ((LibraryNodePrimaryIndex) root).getAllChildren().size()){
				print_leaf(    ( (LibraryNodeLeaf)  ((LibraryNodePrimaryIndex) root).getChildrenAt(i) )      );
				i++;
			}
		}
		else{
			i=0;
			while(i< ((LibraryNodePrimaryIndex) root).getAllChildren().size()){
				print_internal(        ((LibraryNodePrimaryIndex)       ((LibraryNodePrimaryIndex) root).getChildrenAt(i)  )             );
				i++;
			}
		}
		

	}
public void print_leaf(LibraryNodeLeaf root){
		int i=0;
		while(i< ((LibraryNodeLeaf) root).bookCount()){
			
			System.out.println(((LibraryNodeLeaf) root).bookAtIndex(i).fullName());
			
			
			i++;
		}
}

	public void printPrimaryLibrary() {
		
		// add methods to print the primary B+ tree in Depth-first order
		// N - L - R

		  if (primaryRoot != null) {
				
				if(primaryRoot.getType()==LibraryNodeType.Leaf){
					print_leaf(   ((LibraryNodeLeaf) primaryRoot));
				}
				else{
					print_internal(((LibraryNodePrimaryIndex) primaryRoot));
				}
				
		  }

		
		
	}
	
	public void printSecondaryLibrary() {
		
		// add methods to print the secondary B+ tree in Depth-first order
		
	}
}
