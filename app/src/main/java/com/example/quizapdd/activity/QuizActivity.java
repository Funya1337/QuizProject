package com.example.quizapdd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapdd.Preference.AppPreference;
import com.example.quizapdd.R;
import com.example.quizapdd.adapters.QuizAdapter;
import com.example.quizapdd.constants.AppConstants;
import com.example.quizapdd.listeners.ListitemClickListener;
import com.example.quizapdd.models.quiz.QuizModel;
import com.example.quizapdd.utilities.ActivityUtilities;
import com.example.quizapdd.utilities.BeatBox;
import com.example.quizapdd.utilities.DialogUtilities;
import com.example.quizapdd.utilities.SoundUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends BaseActivity implements  DialogUtilities.OnCompleteListener {

    private Activity mActivity;
    private Context mContext;
    private ImageButton btnSpeaker;
    private Button btnNext;
    private RecyclerView mRecyclerQuiz;
    private TextView tvQuestionText;
    private TextView tvQuestionTitle;
    private ImageView imgFirstLife, imgSecondLife, imgThirdLife, imgFourthLife, imgFifthLife;
    public static final String EXTRA_MESSAGE = "com.example.quizapdd.MESSAGE";

    private QuizAdapter mAdapter = null;
    private List<QuizModel> mItemList;
    ArrayList<String> mOptionList;
    ArrayList<String> mBackgroundColorList;
    ArrayList<String> mResultList;

    private int mQuestionPosition = 0;
    private int mQuestionsCount = 0;
    private int mScore = 0, mWrongAns = 0, mSkip = 0;
    private int mLifeCounter = 5;
    private boolean mUserHasPressed = false;
    private boolean mIsSkipped = false, mIsCorrect = false;
    private String mQuestionText, mGivenAnsText, mCorrectAnsText, mCategoryId;

    private BeatBox mBeatBox;
    private List<SoundUtilities> mSounds;
    private boolean isSoundOn;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        loadData();
        initListener();

    }

    private void initVar() {
        mActivity = QuizActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mCategoryId = intent.getStringExtra(AppConstants.BUNDLE_KEY_INDEX);
        }

        mItemList = new ArrayList<>();
        mOptionList = new ArrayList<>();
        mBackgroundColorList = new ArrayList<>();
        mResultList = new ArrayList<>();
        //TODO: mResultList = new ArrayList<>();

        mBeatBox = new BeatBox(mActivity);
        mSounds = mBeatBox.getSounds();
    }

    private void initView() {
        setContentView(R.layout.activity_quiz);

        imgFirstLife = (ImageView) findViewById(R.id.firstLife);
        imgSecondLife = (ImageView) findViewById(R.id.secondLife);
        imgThirdLife = (ImageView) findViewById(R.id.thirdLife);
        imgFourthLife = (ImageView) findViewById(R.id.fourthLife);
        imgFifthLife = (ImageView) findViewById(R.id.fifthLife);
        btnSpeaker = (ImageButton) findViewById(R.id.btnSpeaker);
        btnNext = (Button) findViewById(R.id.btnNext);

        tvQuestionText = (TextView) findViewById(R.id.tvQuestionText);
        tvQuestionTitle = (TextView) findViewById(R.id.tvQuestionTitle);

        mRecyclerQuiz = (RecyclerView) findViewById(R.id.rvQuiz);
        mRecyclerQuiz.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new QuizAdapter(mContext, mActivity, mOptionList, mBackgroundColorList);
        mRecyclerQuiz.setAdapter(mAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.quiz));
        enableUpButton();
        initLoader();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadData() {
        showLoader();

        isSoundOn = AppPreference.getInstance(mActivity).getBoolean(AppConstants.KEY_SOUND, true);
        setSpeakerImage();

        loadJson();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setSpeakerImage() {
        if (isSoundOn) {
            btnSpeaker.setImageResource(R.drawable.ic_speaker);
        } else {
            btnSpeaker.setImageResource(R.drawable.ic_speaker_not);
        }
    }


    public void initListener() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                isSoundOn = !isSoundOn;
                AppPreference.getInstance(mActivity).setBoolean(AppConstants.KEY_SOUND, isSoundOn);
                setSpeakerImage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserHasPressed) {
                    FragmentManager manager = getSupportFragmentManager();
                    DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.skip_text), getString(R.string.skip_prompt), getString(R.string.yes), getString(R.string.no), AppConstants.BUNDLE_KEY_SKIP_OPTION);
                    dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
                } else {
//                    TODO: updateResultSet();
//                    mScoreTotal = mScore;
//                    mSkipTotal = mSkip;
//                    mWrongAns = mWrongAnsTotal;
                    if (mScore != 0) {
                        updateResultSet(mScore, 1);
                    } else {
                        updateResultSet(mWrongAns, 2);
                    }
                    Log.d("warn", String.valueOf(mScore));
                    Log.d("warn", String.valueOf(mWrongAns));
                    mScore = 0;
                    mWrongAns = 0;
                    Log.d("warn", String.valueOf(mResultList));
                    setNextQuestion();
                }
            }
        });

        mAdapter.setItemClickListener(new ListitemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (!mUserHasPressed) {
                    int clickedAnswerIndex = position;
                    if (mItemList.get(mQuestionPosition).getCorrectAnswer() != -1) {
                        for (int currentItemIndex = 0; currentItemIndex < mOptionList.size(); currentItemIndex++) {
                            if (currentItemIndex == clickedAnswerIndex && currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_GREEN);
                                mScore++;
                                mIsCorrect = true;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                                }
                            } else if (currentItemIndex == clickedAnswerIndex && !(currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer())) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_RED);
                                mWrongAns++;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_SECOND_INDEX));
                                }
                                decreaseLifeAndStatus();
                            } else if (currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstants.COLOR_GREEN);
                                ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(currentItemIndex);
                            }
                        }
                    } else {
                        mBackgroundColorList.set(clickedAnswerIndex, AppConstants.COLOR_GREEN);
                        mScore++;
                        mIsCorrect = true;
                        mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_ZERO_INDEX));
                    }

                    mGivenAnsText = mItemList.get(mQuestionPosition).getAnswers().get(clickedAnswerIndex);
                    mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());

                    mUserHasPressed = true;
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

    }

    public void startScoreCardActivity(ArrayList dataToSend) {
        Intent intent = new Intent(QuizActivity.this, ScoreCardActivity.class);
        intent.putExtra("data", dataToSend);
        QuizActivity.this.startActivity(intent);
    }

    public void updateResultSet(int data, int checker) {
        if (checker == 1)
            mResultList.add("Score");
        if (checker == 2)
            mResultList.add("Wrong");
        if (checker == 3)
            mResultList.add("Skip");
    }

    public void decreaseLifeAndStatus() {
        mLifeCounter--;
        setLifeStatus();
    }

    void increaseLifeAndStatus() {
        if (mLifeCounter < AppConstants.BUNDLE_KEY_MAX_LIFE) {
            mLifeCounter++;
            setLifeStatus();
        }
    }

    public void setLifeStatus() {
        switch (mLifeCounter) {
            case 1:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.GONE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 2:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 3:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 4:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.GONE);
                break;
            case 5:
                imgFirstLife.setVisibility(View.VISIBLE);
                imgSecondLife.setVisibility(View.VISIBLE);
                imgThirdLife.setVisibility(View.VISIBLE);
                imgFourthLife.setVisibility(View.VISIBLE);
                imgFifthLife.setVisibility(View.VISIBLE);
                break;
            default:
                imgFirstLife.setVisibility(View.GONE);
                imgSecondLife.setVisibility(View.GONE);
                imgThirdLife.setVisibility(View.GONE);
                imgFourthLife.setVisibility(View.GONE);
                imgFifthLife.setVisibility(View.GONE);
                break;
        }
    }

    public void setNextQuestion() {
        if (isSoundOn) {
            mBeatBox.play(mSounds.get(AppConstants.BUNDLE_KEY_FIRST_INDEX));
        }
        mUserHasPressed = false;
        if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter > 0) {
            mQuestionPosition++;
            updateQuestionsAndAnswers();
        } else if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter == 0) {
            FragmentManager manager = getSupportFragmentManager();
            DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.reward_dialog_title), getString(R.string.reward_dialog_message), getString(R.string.yes), getString(R.string.no), AppConstants.BUNDLE_KEY_REWARD_OPTION);
            dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
        } else {
            startScoreCardActivity(mResultList);
            //TODO: invoke ScoreCardActivity
        }
    }

    public void updateQuestionsAndAnswers() {
        mOptionList.clear();
        mBackgroundColorList.clear();
        ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(AppConstants.BUNDLE_KEY_ZERO_INDEX);

        mOptionList.addAll(mItemList.get(mQuestionPosition).getAnswers());
        mBackgroundColorList.addAll(mItemList.get(mQuestionPosition).getBackgroundColors());
        mAdapter.notifyDataSetChanged();

        mQuestionText = mItemList.get(mQuestionPosition).getQuestion();

        tvQuestionText.setText(Html.fromHtml(mQuestionText));
        tvQuestionTitle.setText(getString(R.string.quiz_question_title, mQuestionPosition + 1, mQuestionsCount));
    }

    public void quizActivityClosePrompt() {
        FragmentManager manager = getSupportFragmentManager();
        DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.exit), getString(R.string.cancel_prompt), getString(R.string.yes), getString(R.string.no), AppConstants.BUNDLE_KEY_CLOSE_OPTION);
        dialog.show(manager, AppConstants.BUNDLE_KEY_DIALOG_FRAGMENT);
    }

    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
//            FileInputStream fileInputStream = new FileInputStream(getApplicationContext().getFilesDir().getPath() + "/question_set.json");
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstants.QUESTION_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseJson(sb.toString());
    }

    public void parseJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObjMain.getJSONArray(AppConstants.JSON_KEY_QUESTIONNAIRY);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String question = jsonObj.getString(AppConstants.JSON_KEY_QUESTION);
                int correctAnswer = Integer.parseInt(jsonObj.getString(AppConstants.JSON_KEY_CORRECT_ANS));
                String categoryId = jsonObj.getString(AppConstants.JSON_KEY_CATEGORY_ID);

                Log.d("TAG", categoryId.toString());

                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstants.JSON_KEY_ANSWERS);
                ArrayList<String> contents = new ArrayList<>();
                ArrayList<String> backgroundColors = new ArrayList<>();
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String item_title = jsonArray2.get(j).toString();
                    contents.add(item_title);
                    backgroundColors.add(AppConstants.COLOR_WHITE);
                }
                if (mCategoryId.equals(categoryId)) {
                    mItemList.add(new QuizModel(question, contents, correctAnswer, categoryId, backgroundColors));
                }
            }

            mQuestionsCount = mItemList.size();
            Collections.shuffle(mItemList);

            hideLoader();
            updateQuestionsAndAnswers();

        } catch (JSONException e) {
            e.printStackTrace();
            showEmptyView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                quizActivityClosePrompt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        quizActivityClosePrompt();
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstants.BUNDLE_KEY_CLOSE_OPTION)) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_SKIP_OPTION)) {
                mSkip++;
                //TODO: mIsSkipped = true;
                mGivenAnsText = getResources().getString(R.string.skipped_text);
                mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());
                updateResultSet(mSkip, 3);
                mSkip = 0;
                //TODO: updateResultSet();
                setNextQuestion();
            } else if (viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
                //TODO:  mRewardedVideoAd.show();
            }
        } else if (!isOkPressed && viewIdText.equals(AppConstants.BUNDLE_KEY_REWARD_OPTION)) {
            startScoreCardActivity(mResultList);
            //TODO: invoke ScoreCardActivity
            AppPreference.getInstance(mContext).setQuizResult(mCategoryId, mScore);
            AppPreference.getInstance(mContext).setQuizQuestionsCount(mCategoryId, mQuestionsCount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }
}

