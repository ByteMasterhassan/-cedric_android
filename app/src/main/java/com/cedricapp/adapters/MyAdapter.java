package com.cedricapp.adapters;


/*public class MyAdapter extends RecyclerView.Adapter<VideoHolder> {

    private Context context;
    String exercise, exerciseVideo, exerciseDescription;
    private CoachesDataModel mUploads;
    private ImageView imageThumbnail;
    private Bitmap bitmap;
    int listSize;


    public MyAdapter(Context context, CoachesDataModel uploads) {
        this.context = context;
        mUploads = uploads;
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.warm_up, viewGroup, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder videoHolder, @SuppressLint("RecyclerView") int position) {
        CoachesDataModel uploadCurrent = mUploads;
        videoHolder.shimmerFrameLayout.startShimmerAnimation();

        ArrayList<CoachesDataModel.Warmup> warmupList = (ArrayList<CoachesDataModel.Warmup>) uploadCurrent.getWarmup();


        //Integer warmUpCoachId= uploadCurrent.warmup.get(position).coachNumber;
        int index = position;

        videoHolder.txtFileName.setText(uploadCurrent.getWarmup().get(position).name);

        videoHolder.textVideoDuration.setText(uploadCurrent.getWarmup().get(position).getDuration());
     //  Glide.with(context).load(uploadCurrent.getWarmup().get(position).getThumbnail()).into(videoHolder.imageThumbnail);

        System.out.println(uploadCurrent.getWarmup().get(position).name + "Video is there");
        Glide.with(context).load(uploadCurrent.getWarmup().get(position).thumbnail)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException
                                                        e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        videoHolder.shimmerFrameLayout.stopShimmerAnimation();
                        videoHolder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                        videoHolder.mWarmUpLinearLayout.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(videoHolder.imageThumbnail);

        //Picasso.get().load(uploadCurrent.getImgThumbnail()).into(videoHolder.imageThumbnail);
        try {
            // bitmap = uploadCurrent.getImgThumbnail();
            //if (bitmap != null) {
            //videoHolder.imageThumbnail.setImageBitmap(bitmap);

            // }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(throwable);
            System.out.println("Are you there in thumbnail");
        }


        videoHolder.mWarmUpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(uploadCurrent.getWarmup().get(position).name + "kkkkkkkkkk vvv");
                Fragment fragment = new ExerciseDetailsFragment();
                FragmentTransaction mFragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                //   bundle.putInt("listSize", mUploads);
                bundle.putInt("size", listSize);
                bundle.putParcelableArrayList("exerciseList", warmupList);
                bundle.putInt("index", index);
                bundle.putString("position", uploadCurrent.getWarmup().get(position).videoUrl);
                bundle.putString("videoDescription", uploadCurrent.getWarmup().get(position).description);
                bundle.putString("exercise", uploadCurrent.getWarmup().get(position).name); //key and value
                fragment.setArguments(bundle);
                mFragmentTransaction.replace(R.id.navigation_container, fragment);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.commit();
//
////                Intent intent = new Intent(context, ExerciseDetailsFragment.class);
////                intent.putExtra("exercise",uploadCurrent.getTitle());
////                intent.putExtra("position",uploadCurrent.getUrl());
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mUploads.getWarmup().size() != 0) {
            listSize = mUploads.getWarmup().size();
            System.out.println(listSize + "list size ye hy   ////////////////////////////////     ");
            return listSize;

        } else {
            return 0;
        }


    }


}

class VideoHolder extends RecyclerView.ViewHolder {

    TextView txtFileName;
    ImageView imageThumbnail;
    MaterialTextView textVideoDuration;
    LinearLayout mWarmUpLinearLayout;
    ShimmerFrameLayout shimmerFrameLayout;

    VideoHolder(View view) {
        super(view);

        txtFileName = view.findViewById(R.id.textViewWarmUP);
        textVideoDuration = view.findViewById(R.id.textViewWarmUpTime);
        imageThumbnail = view.findViewById(R.id.imageWarmUp);
        mWarmUpLinearLayout = view.findViewById(R.id.warmUpLinearLayout);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout22);


    }

}*/
