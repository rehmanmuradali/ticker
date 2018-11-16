[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Ticker-green.svg?style=flat )]( https://android-arsenal.com/details/1/7322 )

# Ticker
Ticker is an Android Library which can be used for different countdown Timers, and Loaders. 

## Demo

![Demo](https://media.giphy.com/media/51Y11x6QjsTgrF4cds/giphy.gif)

## Setup
1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add the dependency:
```
dependencies {
		implementation 'com.github.rehmanmuradali:ticker:1.0.0'
	}
```



Default styling using xml:
```xml
 <ticker.views.com.ticker.widgets.circular.timer.view.CircularView
        android:id="@+id/circular_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:m_circle_radius="25"
        app:m_cicle_stroke_width="14"
        app:m_arc_stroke_color="@android:color/white"
        app:m_circle_stroke_color="@color/colorPrimary"
        />

```

```
  CircularView  = (CircularView) rootView.findViewById(R.id.circular_view);
  
  //start circular view to rotate
  circularView.startTimer();
  
  // pause circular view and timer
  if(circularView.pauseTimer())
  {
      //Timer Paused
  }
  
  // resume circular view and timer
  circularView.resumeTimer();
  
  // stop circular view and timer
  circularView.stopTimer();
  
```





### Loader without text
```
CircularView  = (CircularView) rootView.findViewById(R.id.circular_view);


CircularView.OptionsBuilder builderWithoutText = 
		new CircularView.OptionsBuilder()
                	.shouldDisplayText(false)
                	.setCounterInSeconds(CircularView.OptionsBuilder.INFINITE);
                        
                
 circularViewWithoutText.setOptions(builderWithoutText);

```


### Loader with custom text

```
CircularView circularViewWithCustomText = findViewById(R.id.circular_view_with_custom_text);
CircularView.OptionsBuilder builderWithoutText = 
                     new CircularView.OptionsBuilder()
                            .setCounterInSeconds(CircularView.OptionsBuilder.INFINITE)
                            .setCustomText("Waiting for Customer");
                
circularViewWithCustomText.setOptions(builderWithoutText);
```

### Loader with Countdown timer
```
circularViewWithTimer = findViewById(R.id.circular_view_with_timer);
CircularView.OptionsBuilder builderWithTimer = 
          new CircularView.OptionsBuilder()
                .shouldDisplayText(true)
                .setCounterInSeconds(50)
                .setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {
                    
                        // Will be called if times up of countdown timer
                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Finished ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTimerCancelled() {
                    
                        // Will be called if stopTimer is called
                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Cancelled ", Toast.LENGTH_SHORT).show();
                    }
                });

circularViewWithTimer.setOptions(builderWithTimer);
```

#### Available Attributes
+ ``m_circle_radius``: circle radius of Circular View
+ ``m_cicle_stroke_width``: stroke width of Circular View in dp
+ ``m_circle_stroke_color``: stroke color of outer Circular View
+ ``m_arc_stroke_color``: color of inner rotating arc
+ ``m_arc_margin``: Inner arc margin from cicle in dp

## LICENSE
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Author
**Rehman Murad Ali**

