package com.robotemplates.cookbook.adapter;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.R;
import com.robotemplates.cookbook.database.model.RecipeModel;
import com.robotemplates.cookbook.listener.AnimateImageLoadingListener;
import com.robotemplates.cookbook.pojo.SubReddit;

import java.util.List;


public class RecipeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private static final int VIEW_TYPE_RECIPE = 1;
	private static final int VIEW_TYPE_FOOTER = 2;

	private List<SubReddit> mRecipeList;
	private List<Object> mFooterList;
	private RecipeViewHolder.OnItemClickListener mListener;
	private int mGridSpanCount;
	private boolean mAnimationEnabled = true;
	private int mAnimationPosition = -1;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListener mImageLoadingListener;


	public RecipeListAdapter(List<SubReddit> recipeList, List<Object> footerList, RecipeViewHolder.OnItemClickListener listener, int gridSpanCount)
	{
		mRecipeList = recipeList;
		mFooterList = footerList;
		mListener = listener;
		mGridSpanCount = gridSpanCount;

		// image caching options
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.placeholder_photo)
				.showImageOnFail(R.drawable.placeholder_photo)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
		mImageLoadingListener = new AnimateImageLoadingListener();
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		// inflate view and create view holder
		if(viewType==VIEW_TYPE_RECIPE)
		{
			View view = inflater.inflate(R.layout.fragment_recipe_list_item, parent, false);
			return new RecipeViewHolder(view, mListener, mImageLoader, mDisplayImageOptions, mImageLoadingListener);
		}
		else if(viewType==VIEW_TYPE_FOOTER)
		{
			View view = inflater.inflate(R.layout.fragment_recipe_list_footer, parent, false);
			return new FooterViewHolder(view);
		}
		else
		{
			throw new RuntimeException("There is no view type that matches the type " + viewType);
		}
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		// bind data
		if(viewHolder instanceof RecipeViewHolder)
		{
			// entity
			SubReddit recipe = mRecipeList.get(getRecipePosition(position));

			// render view
			if(recipe != null)
			{
				((RecipeViewHolder) viewHolder).bindData(recipe);
			}
		}
		else if(viewHolder instanceof FooterViewHolder)
		{
			// entity
			Object object = mFooterList.get(getFooterPosition(position));

			// render view
			if(object != null)
			{
				((FooterViewHolder) viewHolder).bindData(object);
			}
		}

		// set item margins
		setItemMargins(viewHolder.itemView, position);

		// set animation
		setAnimation(viewHolder.itemView, position);
	}


	@Override
	public int getItemCount()
	{
		int size = 0;
		if(mRecipeList!=null) size += mRecipeList.size();
		if(mFooterList!=null) size += mFooterList.size();
		return size;
	}


	@Override
	public int getItemViewType(int position)
	{
		int recipes = mRecipeList.size();
		int footers = mFooterList.size();

		if(position < recipes) return VIEW_TYPE_RECIPE;
		else if(position < recipes+footers) return VIEW_TYPE_FOOTER;
		else return -1;
	}


	public int getRecipeCount()
	{
		if(mRecipeList!=null) return mRecipeList.size();
		return 0;
	}


	public int getFooterCount()
	{
		if(mFooterList!=null) return mFooterList.size();
		return 0;
	}


	public int getRecipePosition(int recyclerPosition)
	{
		return recyclerPosition;
	}


	public int getFooterPosition(int recyclerPosition)
	{
		return recyclerPosition - getRecipeCount();
	}


	public int getRecyclerPositionByRecipe(int recipePosition)
	{
		return recipePosition;
	}


	public int getRecyclerPositionByFooter(int footerPosition)
	{
		return footerPosition + getRecipeCount();
	}


	public void refill(List<SubReddit> recipeList, List<Object> footerList, RecipeViewHolder.OnItemClickListener listener, int gridSpanCount)
	{
		mRecipeList = recipeList;
		mFooterList = footerList;
		mListener = listener;
		mGridSpanCount = gridSpanCount;
		notifyDataSetChanged();
	}


	public void stop()
	{

	}


	public void setAnimationEnabled(boolean animationEnabled)
	{
		mAnimationEnabled = animationEnabled;
	}


	private void setAnimation(final View view, int position)
	{
		if(mAnimationEnabled && position>mAnimationPosition)
		{
			view.setScaleX(0f);
			view.setScaleY(0f);
			view.animate()
					.scaleX(1f)
					.scaleY(1f)
					.setDuration(300)
					.setInterpolator(new DecelerateInterpolator());

			mAnimationPosition = position;
		}
	}


	private void setItemMargins(View view, int position)
	{
		int height = (int) CookbookApplication.getContext().getResources().getDimension(R.dimen.fragment_recipe_list_recycler_item_size);
		int marginTop = 0;

		if(position<mGridSpanCount)
		{
			TypedArray a = CookbookApplication.getContext().obtainStyledAttributes(null, new int[]{android.R.attr.actionBarSize}, 0, 0);
			marginTop = (int) a.getDimension(0, 0);
			a.recycle();

			height += marginTop;
		}

		ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		marginParams.setMargins(0, marginTop, 0, 0);

		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = height;
	}


	public static final class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView nameTextView;
		private ImageView imageView;
		private OnItemClickListener mListener;
		private ImageLoader mImageLoader;
		private DisplayImageOptions mDisplayImageOptions;
		private ImageLoadingListener mImageLoadingListener;


		public interface OnItemClickListener
		{
			public void onItemClick(View view, int position, long id, int viewType);
		}


		public RecipeViewHolder(View itemView, OnItemClickListener listener, ImageLoader imageLoader, DisplayImageOptions displayImageOptions, ImageLoadingListener imageLoadingListener)
		{
			super(itemView);
			mListener = listener;
			mImageLoader = imageLoader;
			mDisplayImageOptions = displayImageOptions;
			mImageLoadingListener = imageLoadingListener;

			// set listener
			itemView.setOnClickListener(this);

			// find views
			nameTextView = (TextView) itemView.findViewById(R.id.fragment_recipe_list_item_name);
			imageView = (ImageView) itemView.findViewById(R.id.fragment_recipe_list_item_image);
		}


		@Override
		public void onClick(View view)
		{
			mListener.onItemClick(view, getPosition(), getItemId(), getItemViewType());
		}


		public void bindData(SubReddit recipe)
		{
			nameTextView.setText(recipe.getName());
			mImageLoader.displayImage(recipe.getUrlImage(), imageView, mDisplayImageOptions, mImageLoadingListener);
		}
	}


	public static final class FooterViewHolder extends RecyclerView.ViewHolder
	{
		public FooterViewHolder(View itemView)
		{
			super(itemView);
		}


		public void bindData(Object object)
		{
			// do nothing
		}
	}
}
