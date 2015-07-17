package com.robotemplates.cookbook.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="ingredients")
public class IngredientModel
{
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_RECIPE_ID = "recipe_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_UNIT = "unit";

	@DatabaseField(columnName=COLUMN_ID, generatedId=true) private long id;
	@DatabaseField(foreign=true, index=true) private RecipeModel recipe;
	@DatabaseField(columnName=COLUMN_NAME) private String name;
	@DatabaseField(columnName=COLUMN_QUANTITY) private float quantity;
	@DatabaseField(columnName=COLUMN_UNIT) private String unit;


	// empty constructor
	public IngredientModel()
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


	public RecipeModel getRecipe()
	{
		return recipe;
	}


	public void setRecipe(RecipeModel recipe)
	{
		this.recipe = recipe;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public float getQuantity()
	{
		return quantity;
	}


	public void setQuantity(float quantity)
	{
		this.quantity = quantity;
	}


	public String getUnit()
	{
		return unit;
	}


	public void setUnit(String unit)
	{
		this.unit = unit;
	}
}
