package com.robotemplates.cookbook.database.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;


@DatabaseTable(tableName="recipes")
public class RecipeModel
{
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_INTRO = "intro";
	public static final String COLUMN_INSTRUCTION = "instruction";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_SERVINGS = "servings";
	public static final String COLUMN_CALORIES = "calories";
	public static final String COLUMN_FAVORITE = "favorite";

	@DatabaseField(columnName=COLUMN_ID, generatedId=true) private long id;
	@DatabaseField(foreign=true, index=true) private CategoryModel category;
	@DatabaseField(columnName=COLUMN_NAME) private String name;
	@DatabaseField(columnName=COLUMN_INTRO) private String intro;
	@DatabaseField(columnName=COLUMN_INSTRUCTION) private String instruction;
	@DatabaseField(columnName=COLUMN_IMAGE) private String image;
	@DatabaseField(columnName=COLUMN_LINK) private String link;
	@DatabaseField(columnName=COLUMN_TIME) private int time;
	@DatabaseField(columnName=COLUMN_SERVINGS) private int servings;
	@DatabaseField(columnName=COLUMN_CALORIES) private int calories;
	@DatabaseField(columnName=COLUMN_FAVORITE) private boolean favorite;
	@ForeignCollectionField private ForeignCollection<IngredientModel> ingredients; // one to many


	// empty constructor
	public RecipeModel()
	{
	}


	public long getId()
	{
		return id;
	}


	public void setId(long id)
	{
		this.id = id;
	}


	public CategoryModel getCategory()
	{
		return category;
	}


	public void setCategory(CategoryModel category)
	{
		this.category = category;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getIntro()
	{
		return intro;
	}


	public void setIntro(String intro)
	{
		this.intro = intro;
	}


	public String getInstruction()
	{
		return instruction;
	}


	public void setInstruction(String instruction)
	{
		this.instruction = instruction;
	}


	public String getImage()
	{
		return image;
	}


	public void setImage(String image)
	{
		this.image = image;
	}


	public String getLink()
	{
		return link;
	}


	public void setLink(String link)
	{
		this.link = link;
	}


	public int getTime()
	{
		return time;
	}


	public void setTime(int time)
	{
		this.time = time;
	}


	public int getServings()
	{
		return servings;
	}


	public void setServings(int servings)
	{
		this.servings = servings;
	}


	public int getCalories()
	{
		return calories;
	}


	public void setCalories(int calories)
	{
		this.calories = calories;
	}


	public boolean isFavorite()
	{
		return favorite;
	}


	public void setFavorite(boolean favorite)
	{
		this.favorite = favorite;
	}


	public List<IngredientModel> getIngredients()
	{
		List<IngredientModel> list = new ArrayList<>();
		for(IngredientModel m : ingredients)
		{
			list.add(m);
		}
		return list;
	}


	public void setIngredients(ForeignCollection<IngredientModel> ingredients)
	{
		this.ingredients = ingredients;
	}
}
