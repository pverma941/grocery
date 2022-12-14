package com.example.userseller.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.userseller.FilterProductUser;
import com.example.userseller.R;
import com.example.userseller.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList=productsList;
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{
        //ui views
        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,descriptionTv,addToCartTv,
                discountedPriceTv,originalPriceTv;
        private ImageView nextIv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);
            //init ui views
            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            addToCartTv=itemView.findViewById(R.id.addToCartTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);
            nextIv=itemView.findViewById(R.id.nextIv);


        }
    }
    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        final ModelProduct modelProduct=productsList.get(position);
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String originalPrice=modelProduct.getOriginalPrice();
        String productDescription=modelProduct.getProductDescription();
        String productTitle=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String productId=modelProduct.getProductId();
        String timestamp=modelProduct.getTimestamp();
        String productioIcon=modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.discountedNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("$"+originalPrice);
        holder.discountedPriceTv.setText("$"+discountPrice);

        if (discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            //product is not on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productioIcon).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(holder.productIconIv);

        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add product to cart
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product detail
            }
        });

    }

    private double cost=0;
    private double finalCost=0;
    private int quantity= 0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for flag
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        //init layout views
        CircleImageView productIv=view.findViewById(R.id.productIv);
        TextView titleTv,pquantityTv,descriptionTv,discountedNoteTv,originalPriceTv,
                priceDiscountedTv,finalPriceTv,quantityTv;
        ImageButton decrementBtn,incrementBtn;
        Button continueBtn;
        titleTv=view.findViewById(R.id.titleTv);
        pquantityTv=view.findViewById(R.id.pquantityTv);
        descriptionTv=view.findViewById(R.id.descriptionTv);
        discountedNoteTv=view.findViewById(R.id.discountedNoteTv);
        originalPriceTv=view.findViewById(R.id.originalPriceTv);
        priceDiscountedTv=view.findViewById(R.id.priceDiscountedTv);
        finalPriceTv=view.findViewById(R.id.finalPriceTv);
        quantityTv=view.findViewById(R.id.quantityTv);
        decrementBtn=view.findViewById(R.id.decrementBtn);
        incrementBtn=view.findViewById(R.id.incrementBtn);
        continueBtn=view.findViewById(R.id.continueBtn);
        //get data from model
        //final ModelProduct modelProduct=productsList.get(position);
        String productId=modelProduct.getProductId();
        String title=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String description=modelProduct.getProductDescription();
        String discountNote=modelProduct.getDiscountNote();
        String image=modelProduct.getProductIcon();

        String price;
        if (modelProduct.getDiscountAvailable().equals("true")){
            price=modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            //product donts have discount
            price=modelProduct.getOriginalPrice();
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
        }
        cost=Double.parseDouble(price.replaceAll("$",""));
        finalCost=Double.parseDouble(price.replaceAll("$",""));
        quantity=1;

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        //set day
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_add_shopping_cart_24).into(productIv);

        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        }
        titleTv.setText(""+title);
        pquantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText(""+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText(""+modelProduct.getDiscountPrice());
        finalPriceTv.setText(""+finalCost);

        AlertDialog dialog=builder.create();
        dialog.show();
        //increase quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cost;
                quantity++;
                finalPriceTv.setText("$"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost=finalCost-cost;
                    quantity --;
                    finalPriceTv.setText("$"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleTv.getText().toString().trim();
                String priceEach=titleTv.getText().toString().trim().replace("$","");
                String price=titleTv.getText().toString().trim().replace("","");
                String quantity=titleTv.getText().toString().trim();

                //add to db
                addToCart(productId,title,priceEach,price,quantity);
                dialog.dismiss();
            }
        });

    }

    private int itemId=1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"} ))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"} ))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"} ))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"} ))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"} ))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"} ))
                .doneTableColumn();
        
        Boolean b=easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",productId)
                .addData("Item_Price_Each",productId)
                .addData("Item_Price",productId)
                .addData("Item_Quantity",productId)
                .doneDataAdding();

        Toast.makeText(context, "Adding To Cart....", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
}
