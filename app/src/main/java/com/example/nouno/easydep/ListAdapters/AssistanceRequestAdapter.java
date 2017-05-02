package com.example.nouno.easydep.ListAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nouno.easydep.Data.AssistanceRequest;
import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.OnButtonClickListener;
import com.example.nouno.easydep.R;

import java.util.ArrayList;

/**
 * Created by nouno on 30/03/2017.
 */

public class AssistanceRequestAdapter extends ArrayAdapter<AssistanceRequestListItem> {
    private OnButtonClickListener<AssistanceRequestListItem> onEstimateClickListner;
    private OnButtonClickListener<RepairService> onRepairServiceClickListener;
    private OnButtonClickListener<AssistanceRequest> onRequestClickListner;
    private OnButtonClickListener<AssistanceRequest> onCancelClickListner;
    private OnButtonClickListener<AssistanceRequest> onDeleteClickListner;
    private OnButtonClickListener<AssistanceRequestListItem> onQueueClickListner;
    private OnButtonClickListener<AssistanceRequest> onDontShowClickListner;
    public AssistanceRequestAdapter(Context context, ArrayList<AssistanceRequestListItem> list) {
        super(context,0,list);
    }

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final AssistanceRequestListItem assistanceRequest = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.assistance_request_list_item,parent, false);
        }
        TextView nameText = (TextView)item.findViewById(R.id.nameText);
        TextView dateText = (TextView)item.findViewById(R.id.date_text);
        ImageView quotationCircle = (ImageView)item.findViewById(R.id.estimateCircle);
        TextView quotationCircleText = (TextView)item.findViewById(R.id.estimateCircleText);
        TextView quotationText = (TextView)item.findViewById(R.id.estimateText);
        ImageView confirmationCircle = (ImageView)item.findViewById(R.id.confirmationCircle);
        TextView confirmationCircleText = (TextView)item.findViewById(R.id.confirmationCircleText);
        TextView confirmationText = (TextView)item.findViewById(R.id.confirmationText);
        TextView cancelText = (TextView)item.findViewById(R.id.cancel_request);
        TextView requestSentText = (TextView)item.findViewById(R.id.request_sent_text);
        requestSentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestClickListner.onButtonClick(assistanceRequest);
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClickListner.onButtonClick(assistanceRequest);
            }
        });
        nameText.setText(assistanceRequest.getRepairService().getFullName());
        nameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRepairServiceClickListener.onButtonClick(assistanceRequest.getRepairService());
            }
        });

        dateText.setText(assistanceRequest.getTimeString());
        switch (assistanceRequest.getStatus())
        {
            case AssistanceRequestListItem.STATUS_WAITING_QUOTATION:
                uncheckCircle(quotationCircle,quotationCircleText);
                uncheckCircle(confirmationCircle,confirmationCircleText);
                quotationText.setText("Attente du devis");
                quotationText.setOnClickListener(null);
                quotationText.setTextColor(dateText.getTextColors().getDefaultColor());
                confirmationText.setText("Mise en file d'attente");

                confirmationText.setTextColor(dateText.getTextColors().getDefaultColor());
                break;
            case AssistanceRequestListItem.STATUS_QUOTATION_RECEIVED : quotationText.setText("Consulter devis");
                quotationText.setTextColor(getContext().getResources().getColor(android.R.color.holo_orange_dark));
                if (onEstimateClickListner!=null) {
                quotationText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onEstimateClickListner.onButtonClick(assistanceRequest);
                    }
                });}
                uncheckCircle(confirmationCircle,confirmationCircleText);
                makeOrangeCircle(quotationCircle,quotationCircleText);
                confirmationText.setText("Mise en file d'attente");
                confirmationText.setTextColor(dateText.getTextColors().getDefaultColor());
                break;
            case AssistanceRequestListItem.STATUS_IN_QUEUE: quotationText.setText("Devis accepté");
                if (onEstimateClickListner!=null) {
                    quotationText.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            onEstimateClickListner.onButtonClick(assistanceRequest);
                        }
                    });}
                if (onQueueClickListner!=null)
                {
                    confirmationText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onQueueClickListner.onButtonClick(assistanceRequest);
                        }
                    });
                    confirmationText.setTextColor(Color.parseColor("#FFFF8800"));
                }
                quotationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                checkCircle(quotationCircle,quotationCircleText);
                confirmationText.setText(assistanceRequest.getNumberOfPeopleBeforeString());
                makeOrangeCircle(confirmationCircle,confirmationCircleText);
                break;
            case AssistanceRequestListItem.STATUS_REQUEST_REFUSED: quotationText.setText("Demande refusée");
                quotationText.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                makeRedCircle(quotationCircle,quotationCircleText);
                confirmationText.setText("Mise en file d'attente");
                confirmationText.setTextColor(dateText.getTextColors().getDefaultColor());
                cancelText.setText("Supprimer demande");
                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteClickListner.onButtonClick(assistanceRequest);
                    }
                });
                break;
            case AssistanceRequestListItem.STATUS_REPAIR_SERVICE_COMMING : quotationText.setText("Devis accepté");
                quotationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                if (onQueueClickListner!=null)
                {
                    confirmationText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onQueueClickListner.onButtonClick(assistanceRequest);
                        }
                    });

                }
                if (onEstimateClickListner!=null) {
                    quotationText.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            onEstimateClickListner.onButtonClick(assistanceRequest);
                        }
                    });}
                checkCircle(quotationCircle,quotationCircleText);
                confirmationText.setText("Votre dépanneur arrive");
                confirmationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                checkCircle(confirmationCircle,confirmationCircleText);
                break;
                case AssistanceRequestListItem.STATUS_INTERVENTION_CANCELED:quotationText.setText("Devis accepté");
                    quotationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                    confirmationText.setText("Intervention annulée");
                    cancelText.setText("Supprimer demande");
                    cancelText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDeleteClickListner.onButtonClick(assistanceRequest);
                        }
                    });
                    confirmationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                    checkCircle(quotationCircle,quotationCircleText);
                    makeRedCircle(confirmationCircle,confirmationCircleText);
                    break;
                case AssistanceRequestListItem.STATUS_COMPLETED:
                    quotationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                    if (onQueueClickListner!=null)
                    {
                        confirmationText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onQueueClickListner.onButtonClick(assistanceRequest);
                            }
                        });

                    }
                    if (onEstimateClickListner!=null) {
                        quotationText.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                onEstimateClickListner.onButtonClick(assistanceRequest);
                            }
                        });}
                    checkCircle(quotationCircle,quotationCircleText);
                    confirmationText.setText("Intervention terminée");
                    cancelText.setText("Ne plus afficher demande");
                    cancelText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onDontShowClickListner.onButtonClick(assistanceRequest);
                        }
                    });
                    confirmationText.setTextColor(getContext().getResources().getColor(android.R.color.background_dark));
                    checkCircle(confirmationCircle,confirmationCircleText);
                    break;

        }
        return item;
    }

    @SuppressLint("NewApi")
    private void checkCircle (ImageView circle, TextView circleText)
    {
        circle.setImageDrawable(getContext().getDrawable(R.drawable.ic_check_circle_30_0));
        circleText.setVisibility(View.GONE);
    }

    @SuppressLint("NewApi")
    private void uncheckCircle (ImageView circle, TextView circleText)
    {
        circle.setImageDrawable(getContext().getDrawable(R.drawable.gray_circle));
        circleText.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NewApi")
    private void makeOrangeCircle (ImageView circle, TextView circleText)
    {
        circle.setImageDrawable(getContext().getDrawable(R.drawable.orange_circle));
        circleText.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NewApi")
    private void makeRedCircle (ImageView circle, TextView circleText)
    {
        circleText.setVisibility(View.GONE);
        circle.setImageDrawable(getContext().getDrawable(R.drawable.ic_refused_30_0));
    }

    public void setOnEstimateClickListner(OnButtonClickListener<AssistanceRequestListItem> onEstimateClickListner) {
        this.onEstimateClickListner = onEstimateClickListner;
    }

    public void setOnRepairServiceClickListener(OnButtonClickListener<RepairService> onRepairServiceClickListener) {
        this.onRepairServiceClickListener = onRepairServiceClickListener;
    }

    public void setOnRequestClickListner(OnButtonClickListener<AssistanceRequest> onRequestClickListner) {
        this.onRequestClickListner = onRequestClickListner;
    }

    public void setOnCancelClickListner(OnButtonClickListener<AssistanceRequest> onCancelClickListner) {
        this.onCancelClickListner = onCancelClickListner;
    }

    public void setOnDeleteClickListner(OnButtonClickListener<AssistanceRequest> onDeleteClickListner) {
        this.onDeleteClickListner = onDeleteClickListner;
    }

    public void setOnQueueClickListner(OnButtonClickListener<AssistanceRequestListItem> onQueueClickListner) {
        this.onQueueClickListner = onQueueClickListner;
    }

    public void setOnDontShowClickListner(OnButtonClickListener<AssistanceRequest> onDontShowClickListner) {
        this.onDontShowClickListner = onDontShowClickListner;
    }
}
