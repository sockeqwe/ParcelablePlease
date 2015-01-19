package com.hannesdorfmann.parcelableplease.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.parcelableplease.sample.model.Banana;
import com.hannesdorfmann.parcelableplease.sample.model.Fruit;
import com.hannesdorfmann.parcelableplease.sample.model.Orange;

public class MainActivity extends ActionBarActivity {

  private final String KEY_FRUIT = "fruit";

  @InjectView(R.id.selectedFruit) TextView selectedFruitTextView;

  Fruit selectedFruit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    if (savedInstanceState != null){
      selectedFruit = savedInstanceState.getParcelable(KEY_FRUIT);
    }

    updateUi();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(KEY_FRUIT, selectedFruit);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.banana)
  public void onBananaClicked() {
    selectedFruit = new Banana();
    updateUi();
  }

  @OnClick(R.id.orange)
  public void onOrangeClicked() {
    selectedFruit = new Orange();
    updateUi();
  }

  private void updateUi() {
    String fruit = "";
    if (selectedFruit != null) {
      fruit = selectedFruit.toString();
    }
    selectedFruitTextView.setText(fruit);
  }
}
